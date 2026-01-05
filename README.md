## URL Shortener

Maps the original URL to a 10 characters string. For generating unique random 10 characters
string, Base62 encoding is used with current timestamp and `secureRandom.nextInt()` value minimizing
the probability for collisions and ensuring `62^10` possible combinations. If however collision
occurs, InternalServerErrorException is thrown because database accepts only unique values. This can
be further enhanced with implementing a retry mechanism.

Current implementation performs single `SELECT` statement when retrieving the original URL, querying
the database on a single indexed (unique) field `short_url`.

```
GET /api/v1/url?url=https://shrt.com/k93ng3lNTa
Accept: application/json
```

Similarly, creating a short URL involves generating Base62 encoded string and performing `INSERT`.

```
POST /api/v1/url
Content-Type: application/json

{
  "url": "https://original-long-url.com"
}
```
