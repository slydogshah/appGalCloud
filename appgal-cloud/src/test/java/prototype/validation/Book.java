package prototype.validation;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;


public class Book {

    @NotBlank(message="Title may not be blank")
    public String title;

    @NotBlank(message="Author may not be blank")
    public String author;

    @Min(message="Author has been very lazy", value=1)
    public double pages;

    @Email(regexp = ".+@.+\\..+",message="Email is invalid")
    public String email;

    @Digits(integer = 10, fraction = 0)
    public String phone;
}
