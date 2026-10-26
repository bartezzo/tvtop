from tvtop.cli import main


def test_main_returns_zero(capsys):
    assert main([]) == 0
    assert capsys.readouterr().out.strip() == "tvtop"
