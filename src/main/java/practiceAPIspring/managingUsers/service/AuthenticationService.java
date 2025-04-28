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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import practiceAPIspring.managingUsers.constant.StatusMessage;
import practiceAPIspring.managingUsers.dto.request.comonRequest.AuthenticationRequest;
import practiceAPIspring.managingUsers.dto.request.comonRequest.IntrospectRequest;
import practiceAPIspring.managingUsers.dto.request.comonRequest.RefreshRequest;
import practiceAPIspring.managingUsers.dto.response.comonResponse.AuthenticationResponse;
import practiceAPIspring.managingUsers.dto.response.comonResponse.IntrospectResponse;
import practiceAPIspring.managingUsers.dto.response.comonResponse.logoutRequest;
import practiceAPIspring.managingUsers.model.InvalidToken;
import practiceAPIspring.managingUsers.model.User;
import practiceAPIspring.managingUsers.repository.InvalidTokenRepo;
import practiceAPIspring.managingUsers.repository.UserRepo;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepo userRepository;
    PasswordEncoder passwordEncoder;
    InvalidTokenRepo invalidatedTokenRepository;


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
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(authenticated)
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshRequest request)
            throws ParseException, JOSEException {
        var signJWT = verifyToken(request.getToken(), true);
        var jit = signJWT.getJWTClaimsSet().getJWTID();
        var expiriTime = signJWT.getJWTClaimsSet().getExpirationTime();

        InvalidToken invalidatedToken = InvalidToken.builder()
                .id(jit)
                .expiryTime(expiriTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);//log out cho vao bang de refresh lai
        var email = signJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findByEmail(email).orElseThrow(
                () -> new ApplicationContextException("ERROR NOT FOUND USER WITH TOKEN! ")
        );
        var newToken = generateToken(user);
        return AuthenticationResponse.builder()
                .token(newToken)
                .authenticated(true)
                .build();
    }

    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("dev")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(Long.parseLong(VALID_DURATION), ChronoUnit.SECONDS).toEpochMilli()
                ))
                //  oauth2ResourceServer().jwt() mặc định sẽ thêm prefix "SCOPE_"
                //  vào từng scope, nếu bạn không chỉ định rõ cách mapping claims.

                .jwtID(UUID.randomUUID().toString())

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


    public IntrospectResponse introspect(IntrospectRequest request) {

        boolean isValid = true;
        try {
            verifyToken(request.getToken(), false);
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

    public void logout(logoutRequest request){
        try {


            var signToken = verifyToken(request.getToken(), true);
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
            InvalidToken invalidatedToken = InvalidToken
                    .builder()
                    .id(jit)
                    .expiryTime(expiryTime)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);



        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh){
        try {
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);

            var verified = signedJWT.verify(verifier);
            Date exrityTime =(isRefresh)?
                    new Date (signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(Long.parseLong(REFRESH_DURATION), ChronoUnit.SECONDS).getEpochSecond())
                    :signedJWT.getJWTClaimsSet().getExpirationTime();

            if (!(verified && exrityTime.after(new Date()))){
                if (isRefresh) {
                    log.info("Token het han CAN thay lai");
                }else {
                    throw  new IllegalCallerException("TOKEN INVALID!");
                }
            }

            if( invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
                if (isRefresh) {
                    log.info("Token da bi logout truoc can thay lai");
                }else {
                    throw new RuntimeException();
                }
            }

            return  signedJWT;
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
}
