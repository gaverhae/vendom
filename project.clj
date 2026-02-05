(defproject t "app"
  :dependencies [[com.taoensso/sente "1.21.0"]
                 [compojure "1.7.2"]
                 [hiccup "2.0.0"]
                 [http-kit "2.8.1"]
                 [org.clojure/clojure "1.12.0"]
                 [org.clojure/core.async "1.8.741"]
                 [org.clojure/core.match "1.1.1"]
                 [org.slf4j/slf4j-simple "2.0.17"]
                 [ring/ring-defaults "0.7.0"]
                 ;; pinned transitives
                 [org.clojure/tools.reader "1.6.0"]
                 [ring/ring-core "1.15.3"]
                 [ring/ring-codec "1.3.0"]]
  :min-lein-version "2.0.0"
  :main ^:skip-aot t.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :repl {:pedantic? :warn}
             :dev {:source-paths ["dev" "src" "cljs"]
                   :repl-options {:init-ns repl}
                   :resource-paths ["target"]
                   :clean-targets ^{:protect false} ["target"]
                   :dependencies [[no.cjohansen/replicant "2025.12.1"]
                                  [com.bhauman/figwheel-main "0.2.20"]
                                  [com.nextjournal/beholder "1.0.3"]
                                  [org.clojure/clojurescript "1.12.42"]
                                  [org.clojure/tools.namespace "1.5.1"]]}}
  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]})
