# lein-s3-repo

A Leiningen plugin to deploy 3rd party jars to an s3 repository.

## Usage

Use this for user-level plugins:

Put `[lein-s3-repo "0.1.1"]` into the `:plugins` vector of your
`:user` profile, or if you are on Leiningen 1.x do `lein plugin install
lein-s3-repo 0.1.1`.

### Deploying a single jar

Example usage:

    $ lein s3-repo deploy :repo com.acme.repo :url s3://com.acme.repo/release \\
              :coords '[com.acme/product "1.0.0"]' :jar-filename product.jar \\
              :pom-filename pom.xml

NOTE: The `:pom-filename` argument is optional.

Invoking lein-s3-repo in this manner can be done outside of project
scope. See the credentials section below.

### Deploying multiple jars

If your project has 3rd-party jar dependencies that cannot be resolved
by maven, they can be specified in your `project.clj` file:

```clojure
(defproject server "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.0-beta2"]
                 
                 ;; Android GCM
                 [com.google.android.gcm/server "1.0.0"]
                 [org.json.simple/parser "1.1.1"]

                 ;; Datomic
                 [com.datomic/datomic-free "0.8.3619"]]

  :repositories [["com.acme.mvn" {:url "s3://com.acme.mvn/vendor
                                  :creds :gpg}]]

  :plugins [[org.clojars.briprowe/lein-s3-repo "0.1.1"]]

  :3rd-party-jars [{:jar-filename "libs/gcm-server.jar"
                    :coords [com.google.android.gcm/server "1.0.0"]
                    :repo "com.acme.mvn"}
                   {:jar-filename "libs/json-simple-1.1.1.jar"
                    :coords [org.json.simple "1.1.1"]
                    :repo "com.avme.mvn"}
                   {:jar-filename "datomic/datomic-free-0.8.3619.jar"
                    :pom-filename "datomic/pom.xml"
                    :coords [com.datomic/datomic-free "0.8.3619"]
                    :repo "com.acme.mvn"}]
```

## Credentials

When run inside a project, s3-repo uses lieningen to discover
credentials. See
(https://github.com/technomancy/leiningen/blob/master/doc/DEPLOY.md#authentication).

When run outside a project, s3-repo discovers credentials by looking
for them in `S3_REPO_USERNAME` and `S3_REPO_PASSPHRASE`.
Alternatively, you can specify your username and passphrase in the
`:username` and `:passphrase` keywords, respectively.

## License

Copyright © 2013 Brian Rowe

Distributed under the Eclipse Public License, the same as Clojure.
