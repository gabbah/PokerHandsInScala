import collection.immutable.SortedSet

case class Hand(card1: Card, card2: Card, card3: Card, card4: Card, card5: Card) {
  val cards = try {
    SortedSet(card1, card2, card3, card4, card5).toList
  } catch {
    case e: NullPointerException => throw new CardIsNullException
  }
  if (cards.size != 5) throw new DuplicateCardsException

  lazy val highCard = cards.reverse.head

  lazy val isPair = removeRankGroupOfSize(2).size == 3

  lazy val isTwoPair = removeRankGroupOfSize(2).size == 2

  lazy val isStraight = cards.map(_.rank.id).toStream zip Stream.from(cards.head.rank.id) forall {
    case (a, b) => a == b
  }

  lazy val isFlush = cards.forall(card => card.suite.id == cards.head.suite.id )

  lazy val isStraightFlush = isStraight && isFlush

  // TODO: There must be an easier way... can probably be expressed in terms of not full house
  lazy val isFourOfAKind = removeRankGroupOfSize(4).size == 1  // 4 cards -> 1 since they are of equal rank

  lazy val isThreeOfAKind = (cards map(_.rank.id) toSet).size == 3 // 3 cards -> 1 since they are of equal rank



  // Removes groups (grouped on rank, e.g. 7's or Jacks) of cards of a given group size
  private def removeRankGroupOfSize(groupSize: Int): Set[Card] = {
    val groupedByRank = cards.groupBy(_.rank.id).values
    val withoutGroupOfSize = groupedByRank.filterNot(_.size == groupSize)
    withoutGroupOfSize.flatten.toSet
  }
}

class InvalidHandException(msg:String) extends RuntimeException(msg)
class CardIsNullException extends InvalidHandException("No card in a hand may be null")
class DuplicateCardsException extends InvalidHandException("Duplicates cards are not allowed")