package practiceAPIspring.managingUsers.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils implements Serializable {

    @NonFinal
    @Value("${jwt.signerKey}")
    private String signerKey;

    @NonFinal
    @Value("${jwt.valid-duration}")
    private long jwtExpirationInMillis;

    /**
     * Lấy username (subject) từ token.
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, JWTClaimsSet::getSubject);
    }

    /**
     * Lấy ngày hết hạn từ token.
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, JWTClaimsSet::getExpirationTime);
    }

    /**
     * Trích xuất 1 claim bất kỳ từ token bằng claim resolver.
     */
    public <T> T getClaimFromToken(String token, Function<JWTClaimsSet, T> claimsResolver) {
        final JWTClaimsSet claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Kiểm tra token đã hết hạn chưa.
     */
    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Tạo JWT mới từ thông tin user bằng Nimbus.
     */
    public String generateToken(UserDetails userDetails) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpirationInMillis);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userDetails.getUsername())
                    .issueTime(now)
                    .expirationTime(expiryDate)
                    .build();

            JWSSigner signer = new MACSigner(signerKey.getBytes());

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );

            signedJWT.sign(signer);

            return signedJWT.serialize();

        } catch (JOSEException e) {
            throw new RuntimeException("Lỗi khi ký JWT", e);
        }
    }

    /**
     * Kiểm tra token có hợp lệ không (khớp username và chưa hết hạn).
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String usernameFromToken = getUsernameFromToken(token);
        return (usernameFromToken.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Trích xuất toàn bộ claims từ token bằng Nimbus.
     */
    private JWTClaimsSet extractAllClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(new MACVerifier(signerKey.getBytes()))) {
                throw new RuntimeException("Chữ ký không hợp lệ");
            }

            return signedJWT.getJWTClaimsSet();

        } catch (ParseException e) {
            throw new RuntimeException("Token sai định dạng", e);
        } catch (JOSEException e) {
            throw new RuntimeException("Lỗi khi xác minh chữ ký", e);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xử lý token", e);
        }
    }
}
