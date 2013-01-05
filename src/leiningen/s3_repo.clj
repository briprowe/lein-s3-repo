(ns leiningen.s3-repo
  (:require [leiningen.core.main  :as main]
            [cemerick.pomegranate.aether :as aether])
  (:import [org.springframework.build.aws.maven SimpleStorageServiceWagon]))

(aether/register-wagon-factory! "s3" #(SimpleStorageServiceWagon.))

(defn deploy
  "Deploy a given artifact to S3."
  [repo url username passphrase coords jar-file pom-file]
  
  (aether/deploy :repository {repo {:url url :username username :passphrase passphrase}}
                 :coordinates coords
                 :jar-file jar-file
                 :pom-file pom-file))

(defn print-help
  []
  (println "
Leiningen plugin for managing a maven repository hosted on Amazon S3.

deploy Deploy an artifact to S3.
"))

(defn ^:no-project-needed s3-repo
  "Manage a maven repository hosted on Amazon S3."
  [_ command & args]
  (case command
    "deploy" (apply deploy args)
    (print-help)))