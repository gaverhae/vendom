let
  pkgs = import ./nix/nixpkgs.nix;
in
pkgs.mkShell {
  LOCALE_ARCHIVE = if pkgs.stdenv.isLinux then "${pkgs.glibcLocales}/lib/locale/locale-archive" else "";
  buildInputs = with pkgs; [
    bash
    curl
    heroku
    jq
    leiningen
    tailwindcss
  ];
}
