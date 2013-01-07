(ns leiningen.s3-repo
  (:require [leiningen.core.main :as main]
            [leiningen.core.classpath :refer [add-repo-auth]]
            [clojure.java.io :as io]
            [cemerick.pomegranate.aether :as aether])
  (:import [org.springframework.build.aws.maven SimpleStorageServiceWagon]))

(aether/register-wagon-factory! "s3" #(SimpleStorageServiceWagon.))

(def +USERNAME-ENV-VAR+   "S3_REPO_USERNAME")
(def +PASSPHRASE-ENV-VAR+ "S3_REPO_PASSPHRASE")

;; Snatched from leiningen's deploy.clj
(defn- abort-message [message]
  (cond (re-find #"Return code is 405" message)
        (str message "\n" "Ensure you are deploying over SSL.")
        (re-find #"Return code is 401" message)
        (str message "\n" "See `lein s3-repo help` for an explanation of how to"
             " specify credentials.")
        :else message))

(defn get-env-auth
  [creds]
  (let [username (System/getenv +USERNAME-ENV-VAR+)
        passphrase (System/getenv +PASSPHRASE-ENV-VAR+)
        keys (set (keys creds))]
    (if-not (and
             (or (clojure.set/subset? #{:username :password} keys)
                 (clojure.set/subset? #{:username :passphrase} keys))
             (not (nil? username))
             (not (nil? passphrase)))
      (merge creds {:username username :passphrase passphrase})
      creds)))

(defn get-credentials
  [{:keys [repo] :as args}]
  (let [creds (select-keys args [:url :username :password :passphrase :private-key-file])]
    (-> (add-repo-auth [repo creds])
        second
        get-env-auth)))

(defn deploy
  "Deploy a given artifact to S3."
  [{:keys [repo url username passphrase coords jar-filename pom-filename] :as args}]
  (let [jar-file (when jar-filename (io/file jar-filename))
        pom-file (when pom-filename (io/file pom-filename))
        coords (read-string coords)
        creds (get-credentials args)]
    (when (empty? repo)
      (main/abort "Must supply a repository name."))
    (when (empty? url)
      (main/abort "Must supply a URL."))
    (when (empty? coords)
      (main/abort "Must supply a leiningen-style coordinate vector: '[com.acme/product \"1.0.0\"]'"))
    (when-not (and jar-file (.exists jar-file))
      (main/abort "Could not load jar file:" jar-filename))
    (when (and pom-file (not (.exists pom-file)))
      (main/abort "Could not load pom file:" pom-filename))
    
    (try
      (main/info "Deploying" jar-filename "to" repo)
      (aether/deploy :repository {repo creds}
                     :coordinates coords
                     :jar-file jar-file
                     :pom-file pom-file)
      (catch org.sonatype.aether.deployment.DeploymentException e
        (when main/*debug* (.printStackTrace e))
        (main/abort (abort-message (.getMessage e)))))))

(defn read-args
  "Read the strings in key-position into keywords."
  [& args]
  (assert (even? (count args)))
  (reduce #(assoc %1 (read-string (first %2)) (second %2))
          {}
          (partition 2 args)))

(defn print-help
  []
  (println "
Leiningen plugin for managing a maven repository hosted on Amazon S3.

deploy Deploy an artifact to S3. You can convey your S3 credentials in the environment
       variables \"S3_REPO_USERNAME\" and \"S3_REPO_PASSPHRASE\" or by passing them as
       command line arguments.

       Example invocations:
       $ lein s3-repo deploy :repo com.acme.repo :url s3://com.acme.repo/release \\
              :coords '[com.acme/product \"1.0.0\"]' :jar-filename product.jar \\
              :pom-filename pom.xml
       $ lein s3-repo deploy :repo com.acme.repo :url s3://com.acme.repo/release \\
              :coords '[com.acme/product \"1.0.0\"]' :jar-filename product.jar \\
              :username \"LKJOI...\" :passphrase \"lklkasjdl...\"
"))

(defn ^:no-project-needed s3-repo
  "Manage a maven repository hosted on Amazon S3."
  [_ command & args]
  (case command
    "deploy" (apply (comp deploy read-args) args)
    (print-help)))