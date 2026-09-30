package jp.co.nexus;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserRepositoryTest.class,
        UserServiceTest.class,
        UserControllerTest.class
})
public class UserMasterTestSuite {
}