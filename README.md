# B.I.N SNIPER!
<center> ## この MOD を使用して起こったすべての損害に作者は責任は負いません ## </center>
2024年10月23日現在、BAN/Wipeなどの事象はおこっていません

## luna's BinSniper
| version | updated |
| --- | --- |
| v2.0 (Latest) | 2024/10/23 |

# command
| cmd | 説明 | desc | 
| --- | --- | --- | 
| /bs | ヘルプを表示 | show in-game help |

## credits
- [[Tomochie/@NotTomochie]: Base code](https://web.archive.org/web/20220224153353/https://hackmd.io/@Tomochie/BinSniper)
- [[dxxxxy/1.8.9ForgeTemplate]: Remaking build.gradle, and more](https://github.com/dxxxxy/1.8.9ForgeTemplate)
- [[ChatGPT/o1-mini]: helping decrypt](https://chatgpt.com/)

## このMODにどんな変更を加えてもいいのですか？
問題ありません。 <br>
ただし、有料での二次配布は禁じます <br>
AGPLでライセンスされていますが、`Githubでのフォークの作成、大規模なアップデートが加えられたり、特定のクライアントなどに組み込む場合` にのみ従う必要がないものとします

## Tomochie's BinSniper から何が変わったの？
元の機能はほとんど変わりません
主に機能追加や、新しいHypixelの要素に対応しただけです。

## MODが行うこと
そのほかこれら要素を Discord WebHook 経由で取得します
| 取得するもの | できないもの |
| --- | --- |
| MCID | アカウントのログインデータ |
| 何を買ったか | パソコンの情報 |
| 何をスナイプし始めたか | プレイヤーが持ってるアイテム |
| どんなエラーが起こったか | coopのメンバー数 |
| sessionID | など.. |

<strong>sessionIDは身内内での二次配布防止などに使用していました。この機能は `WebHookUrls.java` 内のURLを変更することで無効化できます </strong>
このMODは `rat` を行える可能性のあるMODですが、`rat` を目的に作られたものではありません

## メイン機能
Hypixel Skyblock BIN オークションのスナイプを行います。
 - /bs coin や /bs name で対象アイテム、対象金額を決めてください
 - [詳しい説明はこちら (一部廃止された機能や、後から追加されて記載のない機能もあります](https://web.archive.org/web/20220224153353/https://hackmd.io/@Tomochie/BinSniper)
<video src="https://github.com/user-attachments/assets/cd2f8463-8cef-476a-b4b5-d7df2f66f735"/>

