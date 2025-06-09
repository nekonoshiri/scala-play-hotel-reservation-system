package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}
import scala.concurrent.{ExecutionContext}

import models.BedTypeRepository

/**
 * ベッドタイプに関するコントローラー。
 */
@Singleton
class BedTypeController @Inject()(
  val controllerComponents: ControllerComponents,
  bedTypeRepository: BedTypeRepository,
) (
  // 非同期処理の実行コンテキストとして scala.concurrent.ExecutionContext を利用する
  using ExecutionContext
) extends BaseController:

  /**
   * ベッドタイプ一覧を表示します。
   */
  def index() = Action.async: (request: Request[AnyContent]) =>
    given Request[AnyContent] = request
    bedTypeRepository.list().map: bedTypes =>
      Ok(views.html.bed_type.index(bedTypes))
