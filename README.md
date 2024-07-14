# luna's BinSniper
`Tomochie's Binsniper V12.0` に影響され作成した
Hypixel Skyblock用の Minecraft MOD

## 開発環境
```
IntelliJ IDEA Community Edition 2022.3
Minecraft Forge 1.8.9-11.15.1.2318-1.8.9

java 8
Gradle 3.1
shadow 2.0.4
Minecraft 1.8.9
forgegradle 2.1-SNAPSHOT
```
base Template: [dxxxxy/1.8.9ForgeTemplate](https://github.com/dxxxxy/1.8.9ForgeTemplate)

## 使用方法
これら情報はゲーム内コマンド `/bs help` で表示可能です

- `/bs (/lunabinsniper)` <br>
ベースコマンド
以下のすべてのコマンドはこのコマンドの後に続く

- `/bs help (/bs info)` <br>
コマンドの動作をリストアップして表示する

- `/bs target <ItemName>` <br>
Snipe対象を設定する。
`<ItemName>` に入る文字は Search Auction で検出可能である必要がある
例: `/bs target Accessory Enrichment Swapper` <br>

- `/bs setMin <Minimum Cost>` <br>
Snipe対象となる最低価格 <br>
B までの省略をサポートする (例: `259.9k -> 259900`)<br>
最初に `!` を入れた場合、入力された値 -1 の値を設定する
(例: `!260k -> 259999`) <br>
40000 といれた場合、40k coin未満のアイテムをスナイプしない <br>
例: `/bs setMin 100k`

- `/bs setMax <Maximum Cost>` <br>
Snipe対象となる最大価格 <br>
B までの省略をサポートする (例: `259.9k -> 259900`) <br>
最初に `!` を入れた場合、入力された値 -1 の値を設定する
(例: `!260k -> 259999`) <br>
285000 といれた場合、285001coin以上のアイテムをスナイプしない <br>
例: `/bs setMax 260k`

- `/bs setDelay <delay> <function> <function args>` <br>
Snipeのクールダウンをミリ秒単位で設定する<br>
(例: `/bs setDelay 1000` -> 1秒のクールダウン)
`<function>` にはサポートされるクールダウン設定関数の名称を入れる<br>
現在サポートされている/予定されているものは

<details><summary> ここにリストアップされている </summary>

| 関数名 | 対応バージョン | 使用法 |
| --- | --- | --- |
| random | v1.0 ~ | `/bs setDelay 350 random <rangeMin> <rangeMax>` |
| cycle | v1.0 ~ | `/bs setDelay 350 cycle <delay2> <delay3> <delay4> .. <delay30>` |

### random
`/bs setDelay 350 random <rangeMin> <rangeMax>` <br>
設定した値 + ランダム値 のクールダウンというサイクルで実行する

例: `/bs setDelay 350 random -50 150`

### cycle
`/bs setDelay 350 cycle <delay2> <delay3> ... <delay30>` <br>
設定した delay2, delay3, ... 
に基づきそれらを順番にクールダウンとして設定し実行する

`<delay2>` までは必要だが、それ以降は任意<br>
`<delay30>` まで設定可能

例: `/bs setDelay 350 cycle 500 320 280`

</details>
`<function>` 以降の引数は任意<br>
100以下の Delay や 6000以上の Delay は強制的に
`100 ~ 6000` の間に収束する
例: `/bs setDelay 350`

- `/bs enableAutoBack` <br>
server reboot などで hub またはロビーに戻された際に
Private Island に自動的に戻る

- `/bs setRarity <anyRarity>` <br>
スナイプ対象のレアリティを設定する<br>
サポートする値は
```
`common`, `uncommon`, `rare`, `epic`, `legendary`,
`mythic`, `divine`, `special`, `veryspecial`, `auto`
```
で
`auto` の場合 `No Filter` に設定して動作が実行される <br>
例: `/bs setRarity auto`


## ライセンス
この MOD は [`AGPL-3.0`](/LICENSE) にてラインセンスされており<br>
この MOD のフォークは全てオープンソースである必要があります。

また、このMODを通じて起こったいかなる損害については<strong>一切の責任を負いません。</strong>
<br>(例: BAN, wipe, 間違った購入等)

