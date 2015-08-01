package io.gameq.android;

/**
 * Created by Ludvig on 28/07/15.
 * Copyright GameQ AB 2015
 */
public final class Encoding {

    public static String getStringFromGameStatus(int game, int status) {
        return getStringFromGameStatus(getGameFromInt(game), getStatusFromInt(status));
    }

    public static String getStringFromGameStatus(Game game, Status status) {
        switch (game) {
            case DOTA2:
            switch (status) {
                case OFFLINE: return "Offline";
                case ONLINE: return  "Online";
                case IN_LOBBY: return "In Game Lobby";
                case IN_QUEUE: return "Finding Match";
                case GAME_READY: return "Your Match is Ready";
                case IN_GAME:return "In Match";
            }
            case HON:
            switch (status) {
                case OFFLINE: return "Offline";
                case ONLINE: return  "Online";
                case IN_LOBBY: return "In Game Lobby";
                case IN_QUEUE: return "Finding Match";
                case GAME_READY: return "Your Match is Ready";
                case IN_GAME:return "In Match";
            }
            case CSGO:
            switch (status) {
                case OFFLINE: return "Offline";
                case ONLINE: return  "Online";
                case IN_LOBBY: return "In Game Lobby";
                case IN_QUEUE: return "Finding Match";
                case GAME_READY: return "Your Match is Ready";
                case IN_GAME:return "In Match";
            }
            case HOTS:
            switch (status) {
                case OFFLINE: return "Offline";
                case ONLINE: return  "Online";
                case IN_LOBBY: return "In Game Lobby";
                case IN_QUEUE: return "Finding Match";
                case GAME_READY: return "Your Match is Ready";
                case IN_GAME:return "In Match";
            }
            case LOL:
            switch (status) {
                case OFFLINE: return "Offline";
                case ONLINE: return  "Online";
                case IN_LOBBY: return "In Game Lobby";
                case IN_QUEUE: return "Searching For Match";
                case GAME_READY: return "Your Match is Ready";
                case IN_GAME:return "In Match";
            }

            case NOGAME:
            switch (status) {
                case OFFLINE: return "Offline";
                case ONLINE: return  "Online";
                default: return "Something went wrong";
            }
            default: return "Something went wrong";
        }
    }

    public static String getStringFromGame(int game) {
        return getStringFromGame(getGameFromInt(game));
    }
    public static String getStringFromGame(Game game) {
        switch (game) {
            case DOTA2: return "Dota 2";
            case HON: return "Heroes of Newerth";
            case CSGO: return "Counter Strike Global Offensive";
            case HOTS: return "Heroes of The Storm";
            case LOL: return "League of Legends";
            case NOGAME: return "No Game Active";
            default: return "Unknown Game";
        }
    }



    public static Status getStatusFromInt(int status) {
        switch (status) {
            case 0:
                return Status.OFFLINE;
            case 1:
                return Status.ONLINE;
            case 2:
                return Status.IN_LOBBY;
            case 3:
                return Status.IN_QUEUE;
            case 4:
                return Status.GAME_READY;
            case 5:
                return Status.IN_GAME;
            default:
                return Status.OFFLINE;

        }
    }

    public static Game getGameFromInt(int game) {
        switch (game) {
            case 0:
                return Game.NOGAME;
            case 1:
                return Game.DOTA2;
            case 2:
                return Game.HON;
            case 3:
                return Game.CSGO;
            case 4:
                return Game.HOTS;
            case 5:
                return Game.LOL;
            default:
                return Game.NOGAME;

        }
    }


}
