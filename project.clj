(defproject org.clojars.briprowe/lein-s3-repo "0.1.0"
  :description "Deploy third party artifacts to a maven repository hosted on Amazon S3."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :eval-in-leiningen true
  :dependencies [[org.springframework.build/aws-maven "4.4.0.RELEASE"]]
  :set-version {:updates [{:path "README.md" :no-snapshot true}]})