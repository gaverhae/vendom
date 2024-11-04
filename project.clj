(defproject t "app"
  :dependencies [[com.taoensso/sente "1.19.2"]
                 [compojure "1.7.1"]
                 [hiccup "2.0.0-RC3"]
                 [http-kit "2.8.0"]
                 [org.clojure/clojure "1.12.0"]
                 [org.clojure/core.async "1.6.681"]
                 [org.clojure/core.match "1.1.0"]
                 [org.slf4j/slf4j-simple "2.0.16"]
                 [ring/ring-defaults "0.5.0"]
                 ;; pinned transitives
                 [org.clojure/tools.reader "1.5.0"]
                 [ring/ring-core "1.13.0"]]
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
                   :dependencies [[no.cjohansen/replicant "2024.09.29"]
                                  [com.bhauman/figwheel-main "0.2.18"]
                                  [com.nextjournal/beholder "1.0.2"]
                                  [org.clojure/clojurescript "1.11.132"]
                                  [org.clojure/tools.namespace "1.5.0"]]}}
  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]})
