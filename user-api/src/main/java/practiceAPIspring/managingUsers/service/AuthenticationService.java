package practiceAPIspring.managingUsers.service;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContextException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import practiceAPIspring.managingUsers.constant.StatusMessage;
import practiceAPIspring.managingUsers.dto.request.comonRequest.AuthenticationRequest;
import practiceAPIspring.managingUsers.dto.request.comonRequest.IntrospectRequest;
import practiceAPIspring.managingUsers.dto.request.comonRequest.LogoutRequest;
import practiceAPIspring.managingUsers.dto.request.comonRequest.RefreshRequest;
import practiceAPIspring.managingUsers.dto.response.comonResponse.AuthenticationResponse;
import practiceAPIspring.managingUsers.dto.response.comonResponse.IntrospectResponse;
import practiceAPIspring.managingUsers.dto.response.comonResponse.TokenRefreshResponse;
import practiceAPIspring.managingUsers.model.RefreashToken;
import practiceAPIspring.managingUsers.model.User;
import practiceAPIspring.managingUsers.model.logoutToken;
import practiceAPIspring.managingUsers.repository.LogoutTokenRepo;
import practiceAPIspring.managingUsers.repository.RefreshTokenRepo;
import practiceAPIspring.managingUsers.repository.UserRepo;

//import practiceAPIspring.managingUsers.dto.request.comonRequest.logoutRequest;
//import practiceAPIspring.managingUsers.model.InvalidToken;

//import practiceAPIspring.managingUsers.repository.InvalidTokenRepo;


import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepo userRepository;
    PasswordEncoder passwordEncoder;
    RefreshTokenRepo refreshTokenRepo;
    LogoutTokenRepo logoutTokenRepo;


    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected String VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected String REFRESH_DURATION;


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        // cau hinh tra ve token
        if (!authenticated)
            throw new IllegalArgumentException(StatusMessage.LOGINF);
        // sau khi xacs nhan dung tao token
        var tokenAccess = generateTokenNormal(user);
        var tokenRefresh = generateTokenRefresh(user);
        return AuthenticationResponse.builder()
                .tokenAccess(tokenAccess)
                .tokenRefresh(tokenRefresh)
                .authenticated(authenticated)
                .build();
    }

    public TokenRefreshResponse refreshToken(RefreshRequest request)
            throws ParseException, JOSEException {

        var signJWT = verifyTokenRefresh(request.getTokenRefresh());


        var email = signJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findByEmail(email).orElseThrow(
                () -> new ApplicationContextException("ERROR NOT FOUND USER WITH TOKEN! ")
        );
        var newToken = generateTokenNormal(user);

        return TokenRefreshResponse.builder()
                .tokenAccess(newToken)
                .authenticated(true)
                .build();
    }

    public String generateTokenNormal(User user) {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS512)
                .type(JOSEObjectType.JWT)
                .build();
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("dev")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(Long.parseLong(VALID_DURATION), ChronoUnit.SECONDS).toEpochMilli()
                ))
                //  oauth2ResourceServer().jwt() mặc định sẽ thêm prefix "SCOPE_"
                //  vào từng scope, nếu bạn không chỉ định rõ cách mapping claims.

                .jwtID(UUID.randomUUID().toString())// tao id ngau nhien

                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            System.out.println("Can not create Token!");
            throw new RuntimeException(e);
        }
    }

    public String generateTokenRefresh(User user) {
        String resfreshToken = UUID.randomUUID().toString();
        Date expirationTime = new Date(
                Instant.now().plus(2, ChronoUnit.DAYS).toEpochMilli()
        );

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS512)
                .type(JOSEObjectType.JWT)
                .build();
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("dev")
                .issueTime(new Date())
                .expirationTime(expirationTime)
                .jwtID(resfreshToken)// tao id ngau nhien

                .claim("type","refresh")
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY));
            RefreashToken refreshToken = RefreashToken.builder()
                    .id(resfreshToken)
                    .token(jwsObject.serialize())
                    .expiryTime(expirationTime)
                    .build();
            // lưu vào db
            refreshTokenRepo.save(refreshToken);
            return jwsObject.serialize();
        } catch (JOSEException e) {
            System.out.println("Can not create Token!");
            throw new RuntimeException(e);
        }
    }


    public IntrospectResponse introspect(IntrospectRequest request) {

        boolean isValid = true;
        try {
            verifyTokenNormal(request.getToken(), false);
        }catch (Exception e){
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    private String buildScope(User user) {
        // ghép tất cả các quyền (roles) của user thành một chuỗi, cách nhau bởi dấu cách
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("SCOPE_"+role.getName());
                if(!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add(permission.getRole());
                    });
            });


        return stringJoiner.toString();
    }

    public void logout(LogoutRequest request){
        try {
            var signToken = verifyTokenNormal(request.getNormaltoken(), true);
            String jit = signToken.getJWTClaimsSet().getJWTID();

            logoutToken normalToken = logoutToken
                    .builder()
                    .id(jit)
                    .token(request.getNormaltoken())
                    .build();


            logoutTokenRepo.save(normalToken);
            var signTokenRefresh = verifyTokenNormal(request.getRefreshToken(), true);
            String jitRefresh = signTokenRefresh.getJWTClaimsSet().getJWTID();

            logoutToken refreashToken = logoutToken.builder()
                    .id(jitRefresh)
                    .token(request.getRefreshToken())
                    .build();

            logoutTokenRepo.save(refreashToken);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SignedJWT verifyTokenNormal(String token, boolean isRefresh){
        try {
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);

            var verified = signedJWT.verify(verifier);

            Date exrityTime =(isRefresh)?
                    new Date (signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(Long.parseLong(REFRESH_DURATION), ChronoUnit.SECONDS).getEpochSecond())
                    :signedJWT.getJWTClaimsSet().getExpirationTime();

            if (!verified && !exrityTime.after(new Date())){
                if (isRefresh) {
                    log.info("Token het han CAN thay lai");
                }else {
                    throw  new IllegalCallerException("TOKEN INVALID!");
                }
            }

            if( logoutTokenRepo.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
                    log.info("Token da bi logout truoc can thay lai");
                    throw new RuntimeException();
            }

            return  signedJWT;
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
    private SignedJWT verifyTokenRefresh(String token){
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            var verified = checkTokenRefresh(token);

            Date exrityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            Date currentTime = new Date();
            if (!verified && !currentTime.after(exrityTime)) {
                log.info("Token het han CAN thay lai");
                throw new IllegalCallerException("TOKEN INVALID!");
            }

            if (logoutTokenRepo.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
                log.info("Token đã bị logout trước đó, cần thay lại token.");
                throw new RuntimeException("Token đã bị logout trước đó, cần thay lại.");
            }


            return signedJWT;
        }catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean checkTokenRefresh(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            //check token in database
            String tokenId = signedJWT.getJWTClaimsSet().getJWTID();
            if(refreshTokenRepo.existsById(tokenId)){
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /// // xac thuc tu dong
    ///
    /// //Hàm hỗ trợ trong AuthenticationService để xác thực và extract claims:
    public SignedJWT validateAndExtract(String token) throws ParseException {
        return verifyTokenNormal(token, false);
    }
    public Authentication parseToken(String token) {
        try {
            SignedJWT signedJWT = verifyTokenNormal(token, false);
            String email = signedJWT.getJWTClaimsSet().getSubject();

            var user = userRepository.findByEmail(email).orElseThrow(
                    () -> new ApplicationContextException("User not found")
            );

            // Convert roles + permissions thành GrantedAuthority
            List<GrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                role.getPermissions().forEach(permission -> {
                    authorities.add(new SimpleGrantedAuthority(permission.getRole()));
                });
            });

            // Trả về Authentication object
            return new UsernamePasswordAuthenticationToken(user, null, authorities);

        } catch (Exception e) {
            log.error("Failed to parse token", e);
            return null;
        }
    }

}
