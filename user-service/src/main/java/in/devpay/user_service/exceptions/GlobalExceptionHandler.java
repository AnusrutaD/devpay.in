package in.devpay.user_service.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<Map<String, String>> handleInvalidFormat(InvalidFormatException ex){
        Map<String, String> response = new HashMap<>();

        if (ex.getTargetType().isEnum()) {
            response.put("error", "Invalid value for field: " + ex.getPathReference());
            response.put("message", "Allowed value: " + String.join(", ",
                    Arrays.stream(ex.getTargetType().getEnumConstants())
                            .map(Object::toString)
                            .toList()));
        }
        else {
            response.put("error", "Invalid input format");
            response.put("message", ex.getOriginalMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
