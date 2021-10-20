package common

import community.flock.kmonad.core.common.define.Logger
import org.junit.Assert

object TestLogger : Logger {

    override fun error(string: String) = Assert.assertTrue(string.isNotBlank())

    override fun log(string: String) = Assert.assertTrue(string.isNotBlank())

    override fun warn(string: String) = Assert.assertTrue(string.isNotBlank())

}
