package as.ama

import scala.language.postfixOps
import scala.concurrent.duration._
import scala.concurrent.{ Future, ExecutionContext }
import scala.util.{ Try, Success, Failure }
import akka.actor._
import akka.pattern.ask
import com.typesafe.config._
import com.typesafe.config.Config

/**
 * Main JVM class.
 */
object Main {

  final val mainConfigKey = "ama"
  final val actorSystemName = "ama"

  def main(commandLineArguments: Array[String]) {

    println("Reading configuration...")
    val config = ConfigFactory.load

    val amaBareConfig = config.getConfig(mainConfigKey)

    println("Starting actor system...")
    val actorSystem = ActorSystem(actorSystemName, config)

    val amaRootActorAutomaticDieIfSystemNotCreatedInSeconds = 10
    val broadcasterCreationTimeoutInSeconds = 10
    val amaSystemInitializationTimeoutInSeconds = 10
    createNewAmaRootActor(actorSystem, amaBareConfig, commandLineArguments, amaRootActorAutomaticDieIfSystemNotCreatedInSeconds, broadcasterCreationTimeoutInSeconds, amaSystemInitializationTimeoutInSeconds)
  }

  protected def createNewAmaRootActor(actorSystem: ActorSystem, config: Config, commandLineArguments: Array[String], amaRootActorAutomaticDieIfSystemNotCreatedInSeconds: Int, broadcasterCreationTimeoutInSeconds: Int, amaSystemInitializationTimeoutInSeconds: Int) {
    val createNewAmaRootActorMessage = new Ama.CreateNewAmaRootActor(amaRootActorAutomaticDieIfSystemNotCreatedInSeconds)
    val future: Future[Ama.CreatedAmaRootActor] = Ama(actorSystem).ask(createNewAmaRootActorMessage)(amaRootActorAutomaticDieIfSystemNotCreatedInSeconds seconds).mapTo[Ama.CreatedAmaRootActor]

    import actorSystem.dispatcher

    future.onComplete(createdAmaRootActor(actorSystem.dispatcher, config, commandLineArguments, broadcasterCreationTimeoutInSeconds, amaSystemInitializationTimeoutInSeconds))
  }

  protected def createdAmaRootActor(implicit ec: ExecutionContext, config: Config, commandLineArguments: Array[String], broadcasterCreationTimeoutInSeconds: Int, amaSystemInitializationTimeoutInSeconds: Int): PartialFunction[Try[Ama.CreatedAmaRootActor], Unit] = {
    case Success(createdAmaRootActor) => {

      val future: Future[AmaRootActor.CreatedBroadcaster] = createdAmaRootActor.amaRootActor.ask(AmaRootActor.CreateBroadcaster)(broadcasterCreationTimeoutInSeconds seconds).mapTo[AmaRootActor.CreatedBroadcaster]

      future.onComplete(createdBroadcaster(ec, createdAmaRootActor.amaRootActor, config, commandLineArguments, amaSystemInitializationTimeoutInSeconds))
    }

    case Failure(e) => {
      println(s"Problem while creating AmaRootActor.")
      e.printStackTrace()
      System.exit(-1)
    }
  }

  protected def createdBroadcaster(implicit ec: ExecutionContext, amaRootActor: ActorRef, config: Config, commandLineArguments: Array[String], amaSystemInitializationTimeoutInSeconds: Int): PartialFunction[Try[AmaRootActor.CreatedBroadcaster], Unit] = {
    case Success(createdBroadcaster) => {
      val initMessage = new AmaRootActor.Init(createdBroadcaster.broadcaster, config, commandLineArguments)
      val future: Future[AmaRootActor.InitializationResult] = amaRootActor.ask(initMessage)(amaSystemInitializationTimeoutInSeconds seconds).mapTo[AmaRootActor.InitializationResult]

      future.onComplete(amaInitializationResult)
    }

    case Failure(e) => {
      println(s"Problem while creating broadcaster.")
      e.printStackTrace()
      System.exit(-1)
    }
  }

  protected def amaInitializationResult: PartialFunction[Try[AmaRootActor.InitializationResult], Unit] = {
    case Success(initializationResult) => initializationResult.exception match {

      case Some(exception) => {
        println(s"Problem while initializing AkkaMicroArchitecture")
        exception.printStackTrace()
        System.exit(-1)
      }

      case None =>
    }

    case Failure(e) => {
      println(s"Problem while initializing AkkaMicroArchitecture")
      e.printStackTrace()
      System.exit(-1)
    }
  }
}