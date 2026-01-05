## URL Shortener

Maps the original URL to a 10 characters string. For generating unique random 10 characters
string, Base62 encoding is used with current timestamp and `random.nextInt()` value minimizing the
probability for collisions and ensuring `62^10` possible combinations. If however collision
occurs, InternalServerErrorException is thrown because database accepts only unique values. This can
be further enhanced with implementing a retry mechanism.
