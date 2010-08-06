package test

import org.testng.annotations._
import org.testng.Assert._
import javax.persistence.{EntityManagerFactory, EntityManager, EntityTransaction, Persistence}

import net.teachernews.model._
import net.teachernews.ejb.{MessageEJB, UserEJB, SubscriptionEJB}


/**
 * Test all EJBs implementing DAO.scala, mainly CRUD operations.
 *
 * TestNG and Junit misinterpret Scala's generated equals-methods for emf, em and tx:
 * "Method tx_$eq requires 1 parameters but 0 were supplied in the @Test annotation"
 * As a workaround, the testng Suite (see testng.xml) only includes methods that match
 * 'test.*', therefore all test methods have to be named accordingly.
 *
 * @author Ingo Fischer
 * @version 1.0
 */
object DAOTest {
  var emf: EntityManagerFactory = _
  var em: EntityManager = _
  var tx: EntityTransaction = _	

  var userEJB:UserEJB = new UserEJB
  var messageEJB:MessageEJB = new MessageEJB
  var subscriptionEJB:SubscriptionEJB = new SubscriptionEJB

  /**
   * Initialize EntityManager, set EJB entitymanagers
   */
  @BeforeClass
  def initEntityManager = {
  	DAOTest.emf = Persistence.createEntityManagerFactory("teachernewsPU")
    DAOTest.em = DAOTest.emf.createEntityManager
    
    userEJB.em = DAOTest.em
    messageEJB.em = DAOTest.em
    subscriptionEJB.em = DAOTest.em
  }
	
  @AfterClass
  def closeEntityManager = {
    if (DAOTest.em.isOpen) DAOTest.em.close
    if (DAOTest.emf.isOpen) DAOTest.emf.close
  }
}

@Test
class DAOTest {
	import DAOTest.userEJB
	import DAOTest.messageEJB
	import DAOTest.subscriptionEJB
	
  @Test
  def testUserEJB = {
    val mmuster = new User
    mmuster.name = "Michael"
    mmuster.firstName  = "Mustermann"
    mmuster.title = Title.MR
    mmuster.email = "mmustermann@example.com"
  	mmuster.role = RoleType.TEACHER
    mmuster.setPassword("muster1234")

    val egabler = new User
    egabler.name = "Gabler"
    egabler.firstName = "Erika"
    egabler.title = Title.MS
    egabler.email = "egabler@example.com"
  	egabler.role = RoleType.TEACHER
    egabler.setPassword("gabler1234")
    
    val ifischer = new User
    ifischer.name = "Fischer"
    ifischer.firstName  = "Ingo"
    ifischer.title = Title.MR
    ifischer.email = "ifischer@example.com"
  	ifischer.role = RoleType.STUDENT
    ifischer.setPassword("ifischer1234")
    
    val tx = DAOTest.em.getTransaction
    tx.begin
    userEJB.persist(mmuster)
    userEJB.persist(ifischer)
    userEJB.persist(egabler)
    tx.commit
    
  	val users = userEJB.findBy(User_.name -> "Fischer")
    assertEquals(users.size, 1);
  	
  	val userList = userEJB.findAll
  	assertEquals(userList.size, 3);
  }
  
	/**
	 * Test MessageEJB
	 */
  @Test(dependsOnMethods = Array("testUserEJB")) 
  def testMessageEJB = {
  	val mmuster = userEJB.findBy(User_.email -> "mmustermann@example.com").get(0)
    
    val nachricht = new Message
    nachricht.content = "Ich bin krank!"
    nachricht.regards = mmuster
    nachricht.expirationDate = new java.util.Date
    
    val nachricht2 = new Message
    nachricht2.content = "Ich bin immernoch krank!"
    nachricht2.regards = mmuster
    nachricht2.expirationDate = new java.util.Date
    
    val tx = DAOTest.em.getTransaction
    tx.begin
    messageEJB.persist(nachricht)
    messageEJB.persist(nachricht2)
    tx.commit
    
    assertEquals(messageEJB.findAll.size, 2)
  }
  
  @Test(dependsOnMethods = Array("testUserEJB")) 
  def testSubscriptionEJB = {
  	val mmuster = userEJB.findBy(User_.email -> "mmustermann@example.com").get(0)
  	val ifischer= userEJB.findBy(User_.email -> "ifischer@example.com").get(0)
    
  	val sub = new Subscription
  	sub.sender = mmuster
  	sub.subscriber = ifischer
  	
    val tx = DAOTest.em.getTransaction
    tx.begin
    subscriptionEJB.persist(sub)
    tx.commit
    
    val newSub = subscriptionEJB.findBy(
    		(Subscription_.sender -> mmuster),
    		(Subscription_.subscriber -> ifischer)
		).get(0)
    
    assertNotNull(newSub)
  }
  
}