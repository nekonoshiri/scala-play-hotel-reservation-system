import mill.*
import $ivy.`com.lihaoyi::mill-contrib-playlib:`, mill.playlib.*

object playscalahotelreservationsystem extends RootModule with PlayModule:

  def scalaVersion = "3.6.4"
  def playVersion = "3.0.7"
  def twirlVersion = "2.0.1"

  object test extends PlayTests
