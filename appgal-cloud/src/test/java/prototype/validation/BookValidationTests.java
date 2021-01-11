package prototype.validation;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.stream.Collectors;

@QuarkusTest
public class BookValidationTests {
    private static Logger logger = LoggerFactory.getLogger(BookValidationTests.class);

    @Inject
    private Validator validator;

    @Test
    public void testBookValidation() throws Exception
    {
        Book book = new Book();
        //book.author = "blah";
        //book.title = "blah";
        book.pages = 1.00d;
        book.email = "blah@blah.com";

        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        String message = violations.stream()
                .map(cv -> cv.getMessage())
                .collect(Collectors.joining(", "));
        logger.info(message);

        book.email = "blah@blah";
        violations = validator.validate(book);
        message = violations.stream()
                .map(cv -> cv.getMessage())
                .collect(Collectors.joining(", "));
        logger.info(message);

        long phoneFmt = 123456789L;
        //get a 12 digits String, filling with left '0' (on the prefix)
        DecimalFormat phoneDecimalFmt = new DecimalFormat("0000000000");
        String phoneRawString= phoneDecimalFmt.format(phoneFmt);

        java.text.MessageFormat phoneMsgFmt=new java.text.MessageFormat("({0})-{1}-{2}");
        //suposing a grouping of 3-3-4
        String[] phoneNumArr={phoneRawString.substring(0, 3),
                phoneRawString.substring(3,6),
                phoneRawString.substring(6)};

        System.out.println(phoneMsgFmt.format(phoneNumArr));
    }
}
