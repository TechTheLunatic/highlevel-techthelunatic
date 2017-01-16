package tests;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.Speed;
import hook.Hook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
import threads.dataHandlers.ThreadEvents;

import java.util.ArrayList;

/**
 * @author Rem
 */
public class JUnit_DropModule extends JUnit_Test {
    private GameState mRobot;
    private ScriptManager scriptManager;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        log.debug("JUnit_DeplacementsTest.setUp()");
        mRobot = container.getService(GameState.class);
        //La position de depart est mise dans la Table (l'updtate config va la chercher)
        mRobot.updateConfig();
        mRobot.robot.setPosition(Table.entryPosition);
        mRobot.robot.setOrientation(Math.PI);
        mRobot.robot.setLocomotionSpeed(Speed.SLOW_ALL);
        scriptManager = container.getService(ScriptManager.class);
        mRobot.robot.turn(13*Math.PI/16);
        mRobot.robot.useActuator(ActuatorOrder.REPLIER_PELLETEUSE, false);
        mRobot.robot.useActuator(ActuatorOrder.PRET_PELLE, false);
        // mRobot.robot.moveLengthwise(400);

        container.getService(ThreadEvents.class);
        container.startInstanciedThreads();
    }

    @Test
    public void catchThoseBalls()
    {
        ArrayList<Hook> emptyList = new ArrayList<Hook>();
        try
        {
            //On execute le script
            log.debug("Place les modules dans les bases lunaires");
            scriptManager.getScript(ScriptNames.DROP_MODULE).goToThenExec(1, mRobot, emptyList);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
    @After
    public void finish()
    {
        mRobot.robot.immobilise();
    }
}