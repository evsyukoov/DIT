package app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.stream.Collectors;

@RestController
public class ProxyController {

    RestTemplate restTemplate;

    @Value("${redirect.url}")
    private String address;

    @Value("${redirect.port}")
    private int port;

    public ProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping("/**")
    @ResponseBody
    public ResponseEntity<String> mirrorRest(@RequestBody(required = false) String body,
                                             HttpMethod method, HttpServletRequest request, HttpServletResponse response)
            throws URISyntaxException {

        String requestUrl = request.getRequestURI();

        URI uri = new URI(address);

        uri = UriComponentsBuilder.fromUri(uri)
                //.path(requestUrl)
                //.query(request.getQueryString())
                .build(true).toUri();

        System.out.println(method + ": " + uri);
        System.out.println(body);

//        HttpHeaders headers = new HttpHeaders();
//        Enumeration<String> headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            headers.set(headerName, request.getHeader(headerName));
//        }

        HttpEntity<String> httpEntity = new HttpEntity<>(body);
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.exchange(uri, method, httpEntity, String.class);
        } catch(HttpStatusCodeException e) {
            return ResponseEntity.status(e.getRawStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        if (e instanceof HttpClientErrorException) {
            return new ResponseEntity<>(((HttpClientErrorException) e).getResponseBodyAsString(),
                    ((HttpClientErrorException) e).getStatusCode());
        }
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
