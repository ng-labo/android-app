package xyz.balus

import com.gluonhq.attach.display.DisplayService
import com.gluonhq.attach.util.Platform
import com.gluonhq.charm.glisten.application.AppManager
import com.gluonhq.charm.glisten.application.AppManager.HOME_VIEW
import com.gluonhq.charm.glisten.control.{AppBar, FloatingActionButton}
import com.gluonhq.charm.glisten.mvc.View
import com.gluonhq.charm.glisten.visual.{MaterialDesignIcon, Swatch}
import com.gluonhq.charm.glisten.control.Dialog
import javafx.event.ActionEvent
import javafx.geometry.{Dimension2D, Pos}
import javafx.scene.Scene
import javafx.scene.control.{Button, Label}
import javafx.scene.layout.VBox
import javafx.application.Application
import javafx.stage.Stage

import scala.jdk.FunctionConverters.enrichAsJavaFunction
import scala.util.chaining.scalaUtilChainingOps

object Main:

  def main(args: Array[String]): Unit = Application.launch(classOf[Main], args: _*)

  val DEFAULT_WIDTH: Int = 640
  val DEFAULT_HEIGHT: Int = 480

  lazy val dimensions: Dimension2D =
    if Platform.isDesktop then
      new Dimension2D(DEFAULT_WIDTH, DEFAULT_HEIGHT)
    else
      DisplayService.create
        .map(((ds: DisplayService) => ds.getDefaultDimensions).asJava)
        .orElse(new Dimension2D(DEFAULT_WIDTH, DEFAULT_HEIGHT))

final class Main extends Application:
  import Main._

  private val appManager = AppManager.initialize(postInit)

  override def init(): Unit =
    val label = new Label("Hello")
    val root = new View(new VBox(10, label).tap { _.setAlignment(Pos.TOP_CENTER) }) {
      override protected def updateAppBar(appBar: AppBar): Unit = appBar.setTitleText("Android App by scala")
    }.tap { view =>
      new FloatingActionButton(MaterialDesignIcon.SEARCH.text, (_: ActionEvent) => {
        val dialog = new Dialog()
        dialog.setTitle(new Label("dialog"))
        dialog.setContent(new Label("I believe FloatingActionButton was clicked."))
        val okButton = new Button("OK")
        okButton.setOnAction((e) => dialog.hide())
        dialog.getButtons().add(okButton)
        dialog.showAndWait()
      }).tap { _.showOn(view) }
    }


    appManager.addViewFactory(HOME_VIEW, () => root)

  override def start(stage: Stage): Unit = appManager.start(stage)

  private def postInit(scene: Scene): Unit =
    Swatch.AMBER.assignTo(scene)
    scene.getWindow.setWidth(dimensions.getWidth)
    scene.getWindow.setHeight(dimensions.getHeight)
    scene.getStylesheets().add(Main.getClass.getResource("styles.css").toExternalForm())
