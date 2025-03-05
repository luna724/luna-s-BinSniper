## B.I.N SNIPER!
### この MOD を使用して起こったすべての損害に作者は責任は負いません ## 
当然だよねって感じ

## luna's BinSniper
| version | updated | Download                                  |
| --- | --- |-------------------------------------------|
| v2.0 (Legacy) | 2024/10/23 | `Discord` or `build from Previous Commit` |
| v2.1 (Closed Beta) | 2025/03/?? | `Discord`                                  |
- [Discord](https://discord.gg/bVkYkfZyJZ)

## 使用方法 / コマンド
- `/bs` <br/>
全体設定を変更するためのGUIを開く

- `/bs name <ItemName..>` or `/bs item <ItemName..>` <br/>
スナイプ対象のアイテム名を決める

- `/bs amount <Amount>` <br />
指定個数スナイプしたら停止するように設定する。 0で無効化
この設定が有効の場合 `/bs dynamic_rest` は自動的にオフになる

- `/bs coin <MaximumCoin>` <br />
指定コイン以下のアイテムを購入する

- `/bs swapmode` <br />
FASTMODE, ALLMODEの切り替え

- `/bs message` <br />
うるさいメッセージを表示するかどうか

- `/bs reconnect` <br />
一度買ったら終わるか、それ以降も続けるか

- `/bs forcestop` <br />
びんすなの強制停止

- `/bs uuidmode` <br />
UUIDに基づいて購入済みかどうかを検出する

- `/bs binsleep` or `/bs sleepoptimization` <br />
Grace Period中のアイテムを無視するか購入するか

- `/bs me` <br />
現在のユーザーについての詳細を取得

- `/bs npe` <br />
NullPointerExceptionに対する追加処置
v2.1 から廃止された

- `/bs category <CategoryInt>` <br />
スナイプ対象が売っているカテゴリの番号を指定する
番号はAuctionHouseで表示されるカテゴリを上から数えた値 <br />
(`例: Weapons = 1`)

- `/bs timeout <TimeoutMillSeconds>` <br />
なにかしらが起きて勝手に閉じたりしたときのタイムアウト時間

- `/bs delay <DelayMillSeconds>` <br />
各操作間のクールダウン
Pingより高く設定しないとラグで死ぬ

- `/bs back_compatibility` <br />
古いアクセサリーについているリフォージに対する対応

- `/bs s` <br />
現在の設定の表示
新しい機能の値はサポートしていない

- `/bs save <PresetName>`　<br />
現在の設定のプリセットへの保存
新しい機能の値はサポートしていない

- `/bs load <PresetName>`　<br />
保存したプリセットの読み込み

- `/bs list`　<br />
保存したプリセットのリスト

- `/bs delete <PresetName>`　<br />
保存したプリセットの削除

- `/bs limbo`　<br />
disconnect.spam でLimboに飛ぶ
v2.1で廃止された

## credits
- [[Tomochie/@NotTomochie]: Base code](https://web.archive.org/web/20220224153353/https://hackmd.io/@Tomochie/BinSniper)

## Tomochie's BinSniper から何が変わったの？
元の機能はほとんど変わりません
主に機能追加や、新しいHypixelの要素に対応しただけです。

- 2.1からは機能自体は変わりませんがコードが全く異なるものとなりました

## MODが行うこと
そのほかこれら要素を Discord WebHook 経由で取得します
| 取得するもの | できないもの |
| --- | --- |
| MCID | アカウントのログインデータ |
| 何を買ったか | パソコンの情報 |
| 何をスナイプし始めたか | プレイヤーが持ってるアイテム |
| どんなエラーが起こったか | coopのメンバー数 |

## メイン機能
Hypixel Skyblock BIN オークションのスナイプを行います。
 - /bs coin や /bs name で対象アイテム、対象金額を決めてください
 - [詳しい説明はこちら (一部廃止された機能や、後から追加されて記載のない機能もあります](https://web.archive.org/web/20220224153353/https://hackmd.io/@Tomochie/BinSniper)
<video src="https://github.com/user-attachments/assets/cd2f8463-8cef-476a-b4b5-d7df2f66f735"/>

