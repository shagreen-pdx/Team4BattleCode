package team4player;

import battlecode.common.RobotController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.scalatest.Ignore;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class VaporatorTest {
    @InjectMocks
    Vaporator vaporator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void testTakeTurn() throws Exception {
        vaporator.takeTurn();
    }
    @Test(expected = NullPointerException.class)
    public void testTakeTurn2() throws Exception {
        vaporator.takeTurn();
    }
}
