package com.apigateway.gateway.config;



import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import reactor.core.publisher.Mono;

@Component
public class AuthGlobaleFilter implements GlobalFilter {
    final Logger logger = LoggerFactory.getLogger(AuthGlobaleFilter.class);
   // final RestTemplate restTemplate;
   
    @Value("926D96C90030DD58429D2751AC1BDBBC")
	private String jwtSecret;

	@Value("864000000")
	private int jwtExpirationMs;

    public AuthGlobaleFilter() {
    	
    }

    @Override
    public Mono filter(ServerWebExchange exchange, GatewayFilterChain chain)  {
    	
        String requestPath = exchange.getRequest().getPath().toString();
        boolean header = exchange.getRequest().getHeaders().containsKey("Authorization");
        boolean authPathIsFound = requestPath.contains("/api/auth/");
        boolean SocketPathIsFound = requestPath.contains("socket");
        boolean getjobPath = requestPath.equals("/JobOffers") ;
        boolean getjobPath2 = requestPath.equals("/JobOffers/state/Active") ;
        boolean status =exchange.getRequest().getMethod().toString().equals("GET");
	    
	boolean getCoursePath = requestPath.equals("/course/getAllCourses") ;
	    
        System.out.println(requestPath );
        System.out.println("and"+ getjobPath);
        System.out.println(" and " + exchange.getRequest().getMethod().toString().equals("GET"));
         
         
        if ((getjobPath2 && status) || (getCoursePath && status)) {
        	exchange.getResponse().setStatusCode(HttpStatus.OK);
        }
        else if (!authPathIsFound && !SocketPathIsFound ){
            if(header) {
            	List<String> headers = exchange.getRequest().getHeaders().get("Authorization");
            	String[] parts = headers.get(0).split(" ");
            	if (parts.length == 2 && "Bearer".equals(parts[0])) {
                  if (validateJwtToken(parts[1])) {
        	          exchange.getResponse().setStatusCode(HttpStatus.ACCEPTED);
                  } else {
        	         exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                     return exchange.getResponse().setComplete();
                  }
            	}
            	else {
       	         exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                 }
              }
            else if(!header) {
   	            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
             }
        }
        	
        
        		 
        
       return chain.filter(exchange);
    }
    
    
    public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}
    
    
   
	
}
