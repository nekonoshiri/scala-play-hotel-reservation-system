package models

/** ベッドタイプ。ベッドの種類を表します。
  */
sealed trait BedType:
  /** 名前。 */
  def name: String

  /** 説明。 */
  def description: String

/** まだ ID が振られていないベッドタイプ。
  *
  * @param name @see [[BedTypeTrait.name]]
  * @param description @see [[BedTypeTrait.description]]
  */
case class InitialBedType(
    name: String,
    description: String
) extends BedType:
  /** ID を指定して [[IdentifiedBedType]] に変換します。 */
  def withId(id: Long): IdentifiedBedType =
    IdentifiedBedType(id, name, description)

/** 既に ID が振られているベッドタイプ。
  *
  * @param id ID。
  * @param name @see [[BedTypeTrait.name]]
  * @param description @see [[BedTypeTrait.description]]
  */
case class IdentifiedBedType(
    id: Long,
    name: String,
    description: String
) extends BedType
