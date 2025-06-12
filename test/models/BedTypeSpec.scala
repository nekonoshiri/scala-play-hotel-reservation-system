package models

import org.scalatestplus.play.PlaySpec
import org.scalatest.prop.TableDrivenPropertyChecks.*

class InitialBedTypeSpec extends PlaySpec:
  "withId メソッド" must:
    "ID を指定して IdentifiedBedType に変換できること" in:
      val testCases = Table(
        ("name", "description", "id", "expected"),
        ("名前A", "説明A", 1, IdentifiedBedType(1, "名前A", "説明A")),
        ("名前B", "説明B", 2, IdentifiedBedType(2, "名前B", "説明B")),
        ("", "", 3, IdentifiedBedType(3, "", ""))
      )
      forAll(testCases) { (name, description, id, expected) =>
        val bedType = InitialBedType(name, description)
        bedType.withId(id) mustBe expected
      }
