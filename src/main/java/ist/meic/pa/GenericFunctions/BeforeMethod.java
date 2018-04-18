package ist.meic.pa.GenericFunctions;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)  //for methods only
public @interface BeforeMethod {}
