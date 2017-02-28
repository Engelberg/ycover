(defproject ycover "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [rolling-stones "1.0.0-SNAPSHOT"]
                 [tarantella "1.0.0-SNAPSHOT"]
                 [loco "0.3.1"]
                 [better-cond "1.0.1"]
                 [medley "0.8.4"]]
  :jvm-opts ^:replace ["-Xmx1g" "-server"])