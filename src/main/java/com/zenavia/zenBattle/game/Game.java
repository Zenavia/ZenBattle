package com.zenavia.zenBattle.game;

import java.util.UUID;

public class Game {
    // Instance de partie en cours
    private final Team teamA;
    private final Team teamB;
    private GameState state = GameState.WAITING;

    public Game(Team teamA, Team teamB) {
        this.teamA = teamA;
        this.teamB = teamB;
    }

    public void setState(GameState newState) {
        this.state = newState;
    }

    public GameState getState() {
        return state;
    }

    public Team getTeamA() {
        return teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    public Team getOtherTeam(Team team) {
        return team == teamA ? teamB : teamA;
    }

    public Team checkWinner() {
        if (!teamA.isBeaconAlive()) return teamB;
        if (!teamB.isBeaconAlive()) return teamA;
        return null;
    }

    public int totalPlayers(){
        return teamA.getPlayers().size() + teamB.getPlayers().size();
    }

    public void onBeaconDestroyed(Team destroyedTeam){
        destroyedTeam.destroyBeacon();
        setState(GameState.ENDING);
    }

    public Team getTeamOfPlayer(UUID uuid) {
        if (teamA.getPlayers().contains(uuid)) return teamA;
        if (teamB.getPlayers().contains(uuid)) return teamB;
        return null;
    }
}
