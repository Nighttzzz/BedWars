package br.com.cubeland;

public enum EnumGameStatus {
    AWAITING_PLAYERS ("&eAguardando jogadores"),
    STARTING ("&eIniciando partida"),
    IN_PROGRESS ("&aPartida em andamento");

    String text;

    EnumGameStatus(String text) {
        this.text = text;
    }

    public EnumGameStatus getStatus() {
        return this;
    }

}
