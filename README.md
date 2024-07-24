# vendom

This template is meant to accelerate the initial setup of Clojure/ClojureScript
web development projects. It is mostly meant for my own personal use, but
you're welcome to pick it up if you think it could be useful to you.

In a nutshell, this is a working combination of [http-kit], [sente], [reagent],
and [compojure].

[http-kit]: https://github.com/http-kit/http-kit
[sente]: https://github.com/taoensso/sente
[reagent]: https://github.com/reagent-project/reagent
[compojure]: https://github.com/weavejester/compojure

The overall structure of the project is:

- a Clojure Ring server that mostly serves static files and some generated HTML.
- a Clojure sente server loop where most of the application logic would live.
- a ClojureScript event loop that manages a single state representation and
  reacts to UI-driven events as well as server-sent events. Occasional local
  state via ratoms is acceptable but should be minimal.

## Getting started

Click the fork button (this is a GitHub template), change or remove the
copyright notice at the end of this file, change the LICENSE file to an
appropriate one (or just remove it if you don't want to publish your work under
a license), and rewrite this README.md file to be about your project.

Then:

```console
$ direnv allow
[...]
$ lein repl
[...]
repl=> (go)
```

should open a browser with a running website. For active development, you'll also need:

```console
$ tailwindcss -i tailwind.css -o target/public/css/style.css --watch
```

running in another terminal.

## What's in this?

This template is a combination of a number of separate pieces. Here is a quick
overview.

### Tooling

The project tooling is managed by [Nix], with the development environment
activated by [direnv]. Very little familiarity with either [Nix] or [direnv] is
needed to work on this project, though.

[Nix]: https://nixos.org/download/
[direnv]: https://direnv.net

Executable files under `bin` will be added to your `PATH` upon activating
[direnv]. If you need project-level credentials, you can put them in a
`.envrc.private` file, which will be loaded as a Bash file when creating your
project environment (i.e. `export MY_SECRET_ENV_VAR=secret-password`).

> Note that [Nix] is used exclusively as a tool manager, which is to say it is
> providing us with an environment in which we have a known, fixed version of
> the JVM, Leiningen, and various other shell utilities. We are **NOT** trying
> to use [Nix] to manage our Clojure, JavaScript, or Java dependencies, nor to
> actually build our application. I have found that [Nix] yields a tremendously
> good benefit/effort ratio for tool management, whereas using it for building
> or dependency management requires a much higher investment in learning [Nix]
> itself.
>
> If you're interested, you can read more on [my blog] for my personal thoughts
> on how to best use both [direnv][direnv-blog] and [Nix][Nix-blog] in this
> context.

[my blog]: https://cuddly-octo-palm-tree.com
[direnv-blog]: https://cuddly-octo-palm-tree.com/posts/2021-12-12-tyska-direnv/
[Nix-blog]: https://cuddly-octo-palm-tree.com/posts/2021-12-19-tyska-nix-shell/

You should run [`update-nixpkgs`](bin/update-nixpkgs) regularly.

## Heroku

This template includes a working configuration for deploying to [Heroku]. If
you're not interested in deploying to Heroku, you can remove the `Procfile` and
`system.properties` file, and may find you no longer have any use for
`bin/build` either.

[Heroku]: https://heroku.com

Deploying to Heroku should be as simple as creating an application on the
Heroku side and pushing this project to it.

## Tailwind CSS

This project is set up to use [Tailwind] for all of its CSS needs. If you don't
know about it yet, I strongly recommend taking a look. It synergizes very well
with a component approach to ClojureScript development.

[Tailwind]: https://tailwindcss.com

## figwheel-main

This project makes use of [figwheel-main]. The provided configuration should
work out-of-the-box, but do read the next session on using the REPL. The
`css-dirs` entry is required to get Figwheel to play nice with Tailwind (as
well as the `:clean-targets` line in [project.clj](project.clj)).

[figwheel-main]: https://figwheel.org

This template is simple enough in its approach to ClojureScript compilation
that we do not have more than one Figwheel cofniguration, and just change the
optimization level thourgh CLI arguments in [bin/build](bin/build) instead of
having a dedicated "prod" configuration.

## REPL setup

For development, we have a dedicated `repl` namespace, in which the `lein repl`
command starts. Running the `go` function will start a system that:

- Runs Figwheel in watcher mode. Figwheel will refresh the broswer on each
  ClojureScript and CSS change.
- Reloads server code on file changes. You may still need to refresh your
  browser manually for this to fully reflect, depending on what you changed.

## App name and version

The [project.clj](project.clj) file defines the app name as `t` and the version
string as `app`. I do not believe in code-level version strings (or project
names) for standalone applications, but feel free to change both as you like.

## Contributing

This template is not accepting contributions. For your own derived project,
feel free to change this section as you like.

## License

Copyright © 2024 Gary Verhaegen.

Licensed under the BSD Zero Clause License. See [LICENSE file](LICENSE) in the
project root, or https://opensource.org/licenses/0BSD for full license
information.

The [SPDX](https://spdx.dev) license identifier for this project is `0BSD`.
