## URL Shortener

Maps the original URL to a 10 characters string. For generating unique random 10 characters
string, Base62 encoding is used with current timestamp and `secureRandom.nextInt()` value minimizing
the probability for collisions and ensuring `62^10` possible combinations. If however collision
occurs, InternalServerErrorException is thrown because database accepts only unique values. This can
be further enhanced with implementing a retry mechanism.

Similarly, creating a short URL involves generating Base62 encoded string and performing `INSERT`.

```
POST /api/v1/url
Content-Type: application/json

{
  "url": "https://original-long-url.com"
}
```

Current implementation performs single `SELECT` statement when retrieving the original URL, querying
the database on a single indexed (unique) field `short_url`.

```
GET /k93ng3lNTa
```

## TODO

- [ ] Switch to NoSQL database
- [x] ~~Request URL validation~~
- [ ] Improve response status exception handling
- [ ] Handle storage overflow with generating a short URL for the same URL
- [ ] Add rate limiter
- [ ] Instead of Base62 use hashing algorithm, e.g. MurmurHash, CRC32
- [x] ~~GET should be like this: https://localhost:8080/k93ng3lNTa~~
- [ ] Reduce the number of characters, see how goo.gl did it or bit.ly, e.g. tinyurl uses 8 chars
- [ ] Add URL expiration
- [ ] Implement basic analytics
