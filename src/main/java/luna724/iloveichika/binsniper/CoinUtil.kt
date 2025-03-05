package luna724.iloveichika.binsniper

import kotlin.math.abs

fun formatCoinToString(_coin: Double): String {
    var coin = _coin
    var coinString: String = ""

    // コインがマイナスならメモっとく
    var isNegative = coin < 0
    if (isNegative) coin = abs(coin)


    // 大きい順に変換: B, M, K
    val billion: Double = coin / 1000000000
    val million: Double = coin / 1000000
    val thousand: Double = coin / 1000

    if (billion >= 1) {
        coinString = String.format("%.2fB", billion)
    } else if (million >= 1) {
        coinString = String.format("%.2fM", million)
    } else if (thousand >= 1) {
        coinString = String.format("%.2fk", thousand)
    } else {
        coinString = String.format("%.0f", coin) // 小さな数値はそのまま
    }


    // 負の数の場合、結果に "-" を付ける
    if (isNegative) coinString = "-$coinString"


    // 小数点以下が0なら省略する
    if (coinString.endsWith(".0B") || coinString.endsWith(".0M") || coinString.endsWith(".0k")) {
        coinString= coinString.substring(0, coinString.length - 2)
    }

    return coinString
}

