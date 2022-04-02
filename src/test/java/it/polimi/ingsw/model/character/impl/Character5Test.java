package it.polimi.ingsw.model.character.impl;

import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.util.List;

public class Character5Test extends TestCase {
    @Test
    public void islandNoEntryTest() throws IllegalMoveException {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(0, List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player2), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2), true);
        Character5 character = new Character5();
        player1.addCoin();
        character.use(match, player1.getId(), 0);
        assertEquals(match.getIslands().get(0).getNoEntry(), 1);

        match.getIslands().get(0).addStudent(new Student(PawnColor.RED));
        player1.getSchool().addStudentsToEntrance(List.of(new Student(PawnColor.RED)));
        match.addStudent(PawnColor.RED, player1.getId());
        match.islandInfluence(0, false);
        assertEquals(match.getIslands().get(0).getTowers().size(), 0);
    }

    @Test
    public void islandNoEntryUnionTest() {
        Player player1 = new Player(0, "test1", TowerColor.WHITE, Wizard.BLUE);
        Player player2 = new Player(1, "test2", TowerColor.BLACK, Wizard.GREEN);
        Team team1 = new Team(0, List.of(player1), TowerColor.WHITE);
        Team team2 = new Team(1, List.of(player2), TowerColor.BLACK);
        Match match = new Match(0, List.of(team1, team2), List.of(player1, player2), true);

        match.getIslands().get(0).addNoEntry(1);
        match.getIslands().get(1).addNoEntry(1);
        match.uniteIslands(0, 1, false);
        assertEquals(match.getIslands().get(0).getNoEntry(), 2);
    }
}
