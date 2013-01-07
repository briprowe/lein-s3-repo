# lein-s3-repo

A Leiningen plugin to deploy 3rd party jars to an s3 repository.

## Usage

Use this for user-level plugins:

Put `[lein-s3-repo "0.1.0"]` into the `:plugins` vector of your
`:user` profile, or if you are on Leiningen 1.x do `lein plugin install
lein-s3-repo 0.1.0`.

Example usage:

    $ lein s3-repo deploy :repo com.acme.repo :url s3://com.acme.repo/release \\
              :coords '[com.acme/product \"1.0.0\"]' :jar-filename product.jar \\
              :pom-filename pom.xml

NOTE: The `:pom-filename` argument is optional.

## Credentials

s3-repo discovers credentials by looking for them in
`S3_REPO_USERNAME` and `S3_REPO_PASSPHRASE`. Alternatively, you can
specify your username and passphrase in the `:username` and
`:passphrase` keywords, respectively.

## License

Copyright © 2013 Brian Rowe

Distributed under the Eclipse Public License, the same as Clojure.
