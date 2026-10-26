import argparse

from tvtop import __version__


def main(argv=None):
    parser = argparse.ArgumentParser(prog="tvtop")
    parser.add_argument("--version", action="version", version=__version__)
    parser.parse_args(argv)
    print("tvtop")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
