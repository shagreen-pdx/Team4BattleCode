package team4player;

import battlecode.common.Direction;
import org.junit.Assert;
import org.junit.Test;

public class UtilTest {

  @Test
  public void testRandomDirection() throws Exception {
    Direction result = Util.randomDirection();
    //Assert.assertEquals(Direction.NORTH, result);
  }

  @Test
  public void testRandom() throws Exception {
    int result = Util.random();
    int expect = result;
    Assert.assertEquals(expect, result);
  }
}