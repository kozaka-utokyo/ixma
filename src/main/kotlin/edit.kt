import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import java.io.File
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import java.util.*
import kotlin.collections.ArrayList


@Composable
fun EditWindow(
    rawData: ArrayList<List<String>>,
    filePath: String,
) {
    val stateVertical = rememberScrollState(0)
    val stateHorizontal = rememberScrollState(0)
    var sampleDataNum by remember { mutableStateOf(0) }
    var daySelected = ""
    var modelSelected = ""
    val data = remember { readCsv(File(filePath)) }
    val dataSize = data[0].size
    // cell space
    val wordCounts = mutableListOf(3, 21, 7, 26, 1, 1, 1)
    for ((index, elem) in rawData.withIndex()) {
        elem.forEach {
            if (index == 0) {
                if (wordCounts[4] < it.length) {
                    wordCounts[4] = it.length
                }
            } else if (index == 1) {
                if (wordCounts[4] < it.length) {
                    wordCounts[4] = it.length
                }
            } else {
                elem.forEach {
                    if (wordCounts[5] < it.length) {
                        wordCounts[5] = it.length
                        wordCounts[6] = wordCounts[5] + 5
                    }
                }
            }
        }
    }
    var wordSpaces = FloatArray(wordCounts.size)
    val spaceCountsSum = wordCounts.sum().toFloat()
    val wordSpaceSum = spaceCountsSum * 15
    wordCounts.forEachIndexed { index, s ->
        wordSpaces[index] = s.toFloat() / spaceCountsSum + 0.1.toFloat()
    }
    val correspondences = remember { getCorrespondences(data) }
    val corCols = correspondences.map { it.outCol }
    var dataColumns = data[0].keys.toMutableList()
    dataColumns.add(0, "---")
    var outputNumStrings by remember { mutableStateOf(OutputNumsStrings(correspondences)) }
    var outputSettingStrings by remember { mutableStateOf(OutputSettingStrings(correspondences)) }
    var examUpperNumStrings by remember { mutableStateOf("5000") }
    var isColumnSet by remember { mutableStateOf(true) }
    var manageID by remember { mutableStateOf("") }
    // テーブル描画部分
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(stateVertical).horizontalScroll(stateHorizontal).padding(
            start = 4.dp, end = 8.dp, top = 3.dp, bottom = 8.dp
        ), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.width(wordSpaceSum.dp)) {
            sampleDataNum = dataSelectButton(sampleDataNum, data.size - 1, 0.1F)
            outputButton(
                correspondences,
                outputNumStrings.getAll(),
                outputSettingStrings.getAll(),
                examUpperNumStrings,
                isColumnSet,
                0.1F,
            )
            examUpperNumStrings = EditCell(text = examUpperNumStrings, weight = 0.1F)
            TableCell(text = "$filePath", weight = 0.7F)
        }
        Row(Modifier.width(wordSpaceSum.dp)) {
            TableCell(text = "設定", weight = 1F)
            TableCell(text = "列名有無", weight = 1F)
            TableCell(text = "氏名分割", weight = 1F)
            TableCell(text = "住所分割", weight = 1F)
        }
        Row(Modifier.width(wordSpaceSum.dp)) {
            TableCell(text = "選択", weight = 1F)
            val selectItem = pullDownCellWithItem(setting = listOf("あり", "なし"), weight = 1F)
            isColumnSet = selectItem == "あり"
            pullDownCellWithItem(setting = listOf("要", "済", "項目なし"), weight = 1F)
            pullDownCellWithItem(setting = listOf("要", "済", "項目なし"), weight = 1F)
        }
        Row(Modifier.background(Color.Gray).width(wordSpaceSum.dp)) {
            TableCell(text = "No.", weight = wordSpaces[0])
            TableCell(text = "Tag", weight = wordSpaces[1])
            TableCell(text = "Order", weight = wordSpaces[2])
            TableCell(text = "Setting", weight = wordSpaces[3])
            TableCell(text = "Column", weight = wordSpaces[4])
            TableCell(text = "Example", weight = wordSpaces[5])
            TableCell(text = "Output", weight = wordSpaces[6])
        }
        for ((index, elem) in correspondences.withIndex()) {
            Row(Modifier.width(wordSpaceSum.dp)) {
                var validation = true
                var pullSelected = ""

                // No.
                TableCell(text = elem.cc.toString(), weight = wordSpaces[0])

                // Tag
                if (elem.style == "req") {
                    RequiredTableCell(text = elem.outCol, weight = wordSpaces[1])
                } else if (elem.style == "sub") {
                    CondTableCell(text = elem.outCol, weight = wordSpaces[1])
                } else if (elem.style == "set") {
                    TableCell(text = elem.outCol, weight = wordSpaces[1])
                } else {
                    SubTableCell(text = elem.outCol, weight = wordSpaces[1])
                }

                // Suggestion
                val noOrders =
                    listOf(
                        "モデルID",
                        "イベント種別",
                        "利用上限金額（与信枠）",
                        "決済ステータス",
                        "決済方法",
                        "購入者国コード",
                        "配送先国コード"
                    )
                var numDownRawValue = ""
                if (elem.outCol !in noOrders) {
                    if (elem.style == "set" || elem.sugNums.toList().size == 0) {
                        TableCell(text = "", weight = wordSpaces[2] / 5 * 2)
                    } else {
                        numDownRawValue = numDownCell(nums = elem.sugNums.toList(), weight = wordSpaces[2] / 5 * 2)
                    }
                }
                if ("--selected" in numDownRawValue) {
                    val numDownValue = numDownRawValue
                    if (outputNumStrings.get(index) == "") {
                        validation = outputNumStrings.update(index, numDownValue, dataSize)
                    } else {
                        val tmp = outputNumStrings.get(index) + ",$numDownValue"
                        validation = outputNumStrings.update(index, tmp, dataSize)
                    }
                    if (elem.outCol == "受付番号") {  // for "加盟店管理ID"
                        manageID = numDownValue
                    }
                }

                // Order, (EditCellに投げるまで--selectedはつけたまま)
                var lastOutputNum = 0
                var exValue = "---"
                var outputValue = "---"
                if (elem.style == "set") {
                    if (elem.outCol in noOrders) {
                        TableCell(text = "", weight = wordSpaces[2])
                    } else if (elem.outCol == "決済日時") {  // 連動セル
                        val textValue = outputNumStrings.get(corCols.firstIndex { it == "申請日時" })
                        EditCell(text = textValue, weight = wordSpaces[2] / 5 * 3)
                        validation = outputNumStrings.update(index, textValue, dataSize)
                    } else if (elem.outCol == "加盟店管理ID") {  // 連動セル
                        manageID = EditCell(text = manageID, weight = wordSpaces[2] / 5 * 3)
                        validation = outputNumStrings.update(index, manageID, dataSize)
                    } else if (elem.outCol in listOf(  // 名前側
                            "購入者名前SHA2ハッシュ", "購入者正規化済み名前ハッシュ", "購入者正規化済み名前SHA2ハッシュ"
                        )
                    ) {
                        val textValue = outputNumStrings.get(corCols.firstIndex { it == "購入者名前ハッシュ" })
                        EditCell(text = textValue, weight = wordSpaces[2] / 5 * 3)
                        validation = outputNumStrings.update(index, textValue, dataSize)
                    } else if (elem.outCol in listOf(  // 苗字側
                            "購入者苗字SHA2ハッシュ", "購入者正規化済み苗字ハッシュ", "購入者正規化済み苗字SHA2ハッシュ"
                        )
                    ) {
                        val textValue = outputNumStrings.get(corCols.firstIndex { it == "購入者苗字ハッシュ" })
                        EditCell(text = textValue, weight = wordSpaces[2] / 5 * 3)
                        validation = outputNumStrings.update(index, textValue, dataSize)
                    } else if (elem.outCol in listOf(
                            // 連動セル
                            "購入者氏名文字数",
                            "購入者氏名漢字数",
                            "購入者氏名ひらがな数",
                            "購入者氏名カタカナ数",
                            "購入者氏名アルファベット数",
                            "購入者氏名上記以外文字数",
                            "購入者氏名辞書存在",
                        )
                    ) {
                        var tableText = ""
                        val textValue = outputNumStrings.get(corCols.firstIndex { it == "購入者苗字ハッシュ" })
                        val textValue2 = outputNumStrings.get(corCols.firstIndex { it == "購入者名前ハッシュ" })
                        if (textValue != "") {
                            if (textValue2 != "") {
                                tableText = "$textValue,$textValue2"
                            } else {
                                tableText = textValue
                            }
                        }
                        EditCell(text = tableText, weight = wordSpaces[2] / 5 * 3)
                    } else if (elem.outCol == "購入者PCメールアカウントSHA2ハッシュ") {
                        val textValue = outputNumStrings.get(corCols.firstIndex { it == "購入者PCメールアカウントハッシュ" })
                        EditCell(text = textValue, weight = wordSpaces[2] / 5 * 3)
                        validation = outputNumStrings.update(index, textValue, dataSize)
                    } else {
                        EditCell(text = "", weight = wordSpaces[2] / 5 * 3)
                    }
                } else {
                    val editValueAfter = EditCell(text = outputNumStrings.get(index), weight = wordSpaces[2] / 5 * 3)
                    if (elem.outCol == "受付番号" && outputNumStrings.get(index) != editValueAfter) {  // for "加盟店管理ID"
                        manageID = editValueAfter + "--selected"
                    }
                    validation = outputNumStrings.update(index, editValueAfter, dataSize)
                    if (isValidNumOrComma(dataSize, editValueAfter)) {
                        val orderNums = outputNumStrings.get(index).split(",").map { it.trim() }
                        lastOutputNum = orderNums[orderNums.size - 1].toInt()
                        exValue = data[sampleDataNum][dataColumns[lastOutputNum]].toString()
                        outputValue = ""
                        for (orderNum in orderNums) {
                            outputValue += data[sampleDataNum][dataColumns[orderNum.toInt()]].toString()
                        }
                    }
                }

                // Setting
                var editValueAfter = ""
                if (elem.setting.size > 1) {
                    if (elem.outCol in listOf("申請日時", "目視審査締切日時", "初回支払期限日")) {
                        val dayFormats = getDayFormats()
                        val settingTmp = elem.setting
                        for ((index, dayFormat) in elem.setting.withIndex()) {
                            if (exValue.matches(dayFormats[dayFormat]!!)) {
                                // 近そうなものを先頭にする
                                Collections.swap(settingTmp, 0, index)
                                break
                            }
                        }
                        pullSelected = pullDownCell(setting = settingTmp, weight = wordSpaces[3] / 5)
                        if (elem.outCol != "申請日時" && "--selected" !in pullSelected) {
                            pullSelected = ""  // 初回
                        }
                        editValueAfter = EditCell(text = pullSelected, weight = wordSpaces[3] / 5 * 4)
                        outputSettingStrings.update(index, pullSelected)
                        if (elem.outCol == "申請日時") {  // for 決済日時
                            daySelected = pullSelected
                        }
                    } else {
                        pullSelected = pullDownCell(setting = elem.setting, weight = wordSpaces[3] / 5)
                        editValueAfter = EditCell(text = pullSelected, weight = wordSpaces[3] / 5 * 4)
                        if (elem.outCol == "モデルID") {
                            modelSelected = pullSelected
                        }
                    }
                } else if (elem.outCol == "決済日時") {
                    TableCell(text = daySelected, weight = wordSpaces[3])
                    editValueAfter = daySelected
                } else if (elem.outCol == "イベント種別") {
                    editValueAfter = EditCell(text = "EC", weight = wordSpaces[3])
                } else if (elem.outCol == "利用上限金額（与信枠）") {
                    editValueAfter = EditCell(text = "99999", weight = wordSpaces[3])
                } else if (elem.outCol == "決済ステータス") {
                    editValueAfter = EditCell(text = "00", weight = wordSpaces[3])
                } else if (elem.outCol in listOf("購入者国コード", "配送先国コード")) {
                    editValueAfter = EditCell(text = "JP", weight = wordSpaces[3])
                } else if (elem.outCol in listOf("決済金額", "単価")) {
                    editValueAfter = EditCell(text = "", weight = wordSpaces[3])
                } else if (elem.outCol == "決済方法") {
                    outputValue = if ("--selected" in modelSelected) {
                        getSettlementModels()[modelSelected.replace("--selected", "")].toString() + "--selected"
                    } else {
                        "01"
                    }
                    editValueAfter = EditCell(text = outputValue, weight = wordSpaces[3])
                    outputValue = editValueAfter
                } else {
                    TableCell(text = "", weight = wordSpaces[3])
                }
                outputSettingStrings.update(index, editValueAfter)

                // Column
                if (elem.style == "set") {
                    TableCell(text = "", weight = wordSpaces[4])
                } else {
                    TableCell(text = dataColumns[lastOutputNum], weight = wordSpaces[4])
                }

                // Example
                if (elem.style == "set") {
                    TableCell(text = "", weight = wordSpaces[5])
                } else {
                    TableCell(text = exValue, weight = wordSpaces[5])
                }

                // Output
                if (elem.outCol in noOrders || elem.outCol == "加盟店管理ID") {
                    TableCell(text = "", weight = wordSpaces[6])
                } else {
                    if (elem.outCol in listOf("申請日時", "目視審査締切日時")) {
                        val (regDate, inputFormat) = getRegDate(outputValue, editValueAfter)
                        outputValue = regDate
                        if (inputFormat != "---") {
                            outputSettingStrings.update(index, inputFormat)
                        }
                    } else if (elem.outCol == "モデルID") {
                        outputValue = outputSettingStrings.get(index)
                    }
                    if (!validation && elem.style == "req") {
                        ErrorCell(text = outputValue, weight = wordSpaces[6])
                    } else {
                        TableCell(text = outputValue, weight = wordSpaces[6])
                    }
                }
            }
        }
    }
}


@Composable
fun RowScope.TableCell(
    text: String, weight: Float
) {
    Text(
        text = text,
        modifier = Modifier.border(1.dp, Color.Black).weight(weight).padding(3.dp).height(50.dp)
            .background(Color.DarkGray).wrapContentHeight(),
        textAlign = TextAlign.Center,
        color = Color.White,
        maxLines = 3,
    )
}

@Composable
fun RowScope.ErrorCell(
    text: String, weight: Float
) {
    Text(
        text = text,
        modifier = Modifier.border(1.dp, Color.Black).weight(weight).padding(3.dp).height(50.dp).background(Color.Red)
            .wrapContentHeight(),
        textAlign = TextAlign.Center,
        color = Color.White,
        maxLines = 3,
    )
}


@Composable
fun RowScope.RequiredTableCell(
    text: String, weight: Float
) {
    Text(
        text = text,
        modifier = Modifier.border(1.dp, Color.Black).weight(weight).padding(3.dp).height(50.dp).background(Color.Red)
            .wrapContentHeight(),
        textAlign = TextAlign.Center,
        color = Color.White,
        maxLines = 3,
    )
}

@Composable
fun RowScope.CondTableCell(
    text: String, weight: Float
) {
    Text(
        text = text,
        modifier = Modifier.border(1.dp, Color.Black).weight(weight).padding(3.dp).height(50.dp)
            .background(Color.Magenta).wrapContentHeight(),
        textAlign = TextAlign.Center,
        color = Color.White,
        maxLines = 3,
    )
}

@Composable
fun RowScope.SubTableCell(
    text: String, weight: Float
) {
    Text(
        text = text,
        modifier = Modifier.border(1.dp, Color.Black).weight(weight).padding(3.dp).height(50.dp).background(Color.Gray)
            .wrapContentHeight(),
        textAlign = TextAlign.Center,
        color = Color.White,
        maxLines = 3,
    )
}

@Composable
fun RowScope.EditCell(
    text: String, weight: Float
): String {
    var textFieldString by remember {
        mutableStateOf(text)
    }
    if ("--selected" in text) {
        textFieldString = text.replace("--selected", "")
    }
    TextField(
        value = textFieldString,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Red,
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        placeholder = { Text(text = "csv") },
        onValueChange = { textFieldString = it },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrect = true,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.border(1.dp, Color.Black).weight(weight).padding(3.dp).height(50.dp).fillMaxWidth(),
    )
    return return when (textFieldString) {
        null -> "-1"
        else -> textFieldString
    }
}

@Composable
fun RowScope.numDownCell(nums: List<Int>, weight: Float): String {
    var expanded by remember { mutableStateOf(false) }
    val items = mutableListOf<String>()
    val itr = nums.listIterator()
    var color = Color.DarkGray
    var orPullItem = false
    while (itr.hasNext()) {
        val num = itr.next().toString()
        if (num == "-1") {
            items.add("")
        } else {
            items.add(num)
            color = Color.Gray
            orPullItem = true
        }
    }
    var selectedIndex by remember { mutableStateOf(0) }
    var selectedItem by remember { mutableStateOf("") }
    if (orPullItem) {
        Box(
            modifier = Modifier.border(1.dp, Color.Black).weight(weight).padding(3.dp).height(50.dp).fillMaxSize()
                .clickable(onClick = { expanded = true }), contentAlignment = Alignment.Center
        ) {
            Text(
                text = "",
                modifier = Modifier.border(1.dp, Color.Black).fillMaxWidth().height(50.dp).background(color)
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                color = Color.White,
                maxLines = 3,
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                items.forEachIndexed { index, s ->
                    DropdownMenuItem(onClick = {
                        selectedIndex = index
                        expanded = false
                        selectedItem = "--selected"
                    }) {
                        Text(text = s, color = Color.Black)
                    }
                }
            }
        }
    } else {
        Text(
            text = "",
            modifier = Modifier.border(1.dp, Color.Black).weight(weight).padding(3.dp).height(50.dp)
                .background(Color.DarkGray).wrapContentHeight(),
            textAlign = TextAlign.Center,
            color = Color.White,
            maxLines = 3,
        )
        selectedItem = ""
    }
    val returnValue = items[selectedIndex] + selectedItem
    selectedItem = ""
    return returnValue
}

@Composable
fun RowScope.pullDownCell(setting: List<String>, weight: Float): String {
    var expanded by remember { mutableStateOf(false) }
    var items = setting
    var selectedIndex by remember { mutableStateOf(0) }
    var selectedItem by remember { mutableStateOf("") }
    Box(
        modifier = Modifier.border(1.dp, Color.Black).weight(weight).padding(3.dp).height(50.dp).fillMaxSize()
            .clickable(onClick = { expanded = true }), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "",
            modifier = Modifier.border(1.dp, Color.Black).fillMaxWidth().height(50.dp).background(Color.Gray)
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
            color = Color.White,
            maxLines = 3,
        )
        DropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color.White)
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                    selectedItem = "--selected"
                }) {
                    Text(text = s, color = Color.Black)
                }
            }
        }
    }
    val returnValue = items[selectedIndex] + selectedItem
    selectedItem = ""
    return returnValue
}

@Composable
fun RowScope.pullDownCellWithItem(setting: List<String>, weight: Float): String {
    var expanded by remember { mutableStateOf(false) }
    var items = setting
    var selectedIndex by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier.border(1.dp, Color.Black).weight(weight).padding(3.dp).height(50.dp).fillMaxSize()
            .clickable(onClick = { expanded = true }), contentAlignment = Alignment.Center
    ) {
        Text(
            text = items[selectedIndex],
            modifier = Modifier.border(1.dp, Color.Black).fillMaxWidth().height(50.dp).background(Color.Gray)
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
            color = Color.White,
            maxLines = 3,
        )
        DropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color.White)
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                }) {
                    Text(text = s, color = Color.Black)
                }
            }
        }
    }
    return items[selectedIndex]
}

@Composable
fun RowScope.dataSelectButton(sampleDataNum: Int, maxNum: Int, weight: Float): Int {
    var sampleDataNumTmp by remember { mutableStateOf(sampleDataNum) }
    Box(
        modifier = Modifier.border(1.dp, Color.Black).weight(weight).padding(3.dp).height(50.dp).fillMaxSize()
            .clickable(onClick = {
                if (sampleDataNumTmp >= maxNum) {
                    sampleDataNumTmp = 0
                } else {
                    sampleDataNumTmp = sampleDataNum + 1
                }
            }), contentAlignment = Alignment.Center
    ) {
        var text = sampleDataNumTmp + 1
        Text(
            text = "Data-$text",
            modifier = Modifier.border(1.dp, Color.Black).fillMaxWidth().height(50.dp).background(Color.Gray)
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
            color = Color.White,
            maxLines = 3,
        )
    }
    return sampleDataNumTmp
}

@Composable
fun RowScope.outputButton(
    correspondences: List<correspondence>,
    outputNumStrings: List<String>,
    outputSettingStrings: List<String>,
    examUpperNumStrings: String,
    isColumnSet: Boolean,
    weight: Float,
) {
    Box(
        modifier = Modifier.border(1.dp, Color.Black).weight(weight).padding(3.dp).height(50.dp).fillMaxSize()
            .clickable(onClick = {
                output(
                    correspondences,
                    outputNumStrings,
                    outputSettingStrings,
                    examUpperNumStrings,
                    isColumnSet,
                )
            }), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "XML出力",
            modifier = Modifier.border(1.dp, Color.Black).fillMaxWidth().height(50.dp).background(Color.Gray)
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
            color = Color.White,
            maxLines = 3,
        )
    }
}

// 日時のアウトプット確認用
fun getRegDate(input: String, format: String): RegDate {
    var regDate = ""
    val dateNums = mutableListOf("2000", "00", "00", "00", "00", "00")
    var formatTmp = format
    var inputFormat = ""
    if (input == "---") {
        return RegDate("---", format)
    }
    for (ch in input) {
        val char = ch.toString()
        if (isNumeric(char)) {
            if (formatTmp.substring(0, 1) == "y") {
                if (dateNums[0] == "2000") {
                    dateNums[0] = char
                } else {
                    dateNums[0] += char
                }
                inputFormat += "y"
            } else if (formatTmp.substring(0, 1) == "M") {
                if (dateNums[1] == "00") {
                    dateNums[1] = char
                } else {
                    dateNums[1] += char
                }
                inputFormat += "M"
            } else if (formatTmp.substring(0, 1) == "d") {
                if (dateNums[2] == "00") {
                    dateNums[2] = char
                } else {
                    dateNums[2] += char
                }
                inputFormat += "d"
            } else if (formatTmp.substring(0, 1) == "H") {
                if (dateNums[3] == "00") {
                    dateNums[3] = char
                } else {
                    dateNums[3] += char
                }
                inputFormat += "H"
            } else if (formatTmp.substring(0, 1) == "m") {
                if (dateNums[4] == "00") {
                    dateNums[4] = char
                } else {
                    dateNums[4] += char
                }
                inputFormat += "m"
            } else if (formatTmp.substring(0, 1) == "s") {
                if (dateNums[5] == "00") {
                    dateNums[5] = char
                } else {
                    dateNums[5] += char
                }
                inputFormat += "s"
            }
            formatTmp = formatTmp.drop(1)
        } else {
            formatTmp = formatTmp.drop(1)
            inputFormat += char
        }
        if (formatTmp == "") {
            break
        }
    }
    for ((idx, num) in dateNums.withIndex()) {
        when (idx) {
            0, 1 -> {
                regDate += num
                regDate += "/"
            }

            2 -> {
                regDate += num
                regDate += " "
            }

            3, 4 -> {
                regDate += num
                regDate += ":"
            }

            5 -> {
                regDate += num
            }
        }
    }
    return RegDate(regDate, inputFormat)
}

data class RegDate(val regDate: String, val inputFormat: String)

// 取得位置のモデル
class OutputNumsStrings(correspondences: List<correspondence>) {
    var outputNumStrings = mutableListOf<String>()

    init {
        outputNumStrings = correspondences.map {
            if (it.sugNums.toList().size == 0 || it.style == "sub" || it.style == "") "" else it.sugNums.toList()[0].toString()
        }.toMutableList()
    }

    fun update(idx: Int, numsString: String, dataSize: Int): Boolean {
        var numStringTmp = numsString
        if ("--selected" in numsString) {
            numStringTmp = numsString.replace("--selected", "")
        }
        outputNumStrings[idx] = numsString
        if (idx in listOf(8, 11, 12, 15, 42)) {
            return true
        } else {
            if (isValidNumOrComma(dataSize, numStringTmp)) {
                return true
            }
        }
        return false
    }

    fun get(idx: Int): String {
        return outputNumStrings[idx]
    }

    fun getAll(): List<String> {
        return outputNumStrings
    }
}

// 表示値のモデル
class OutputSettingStrings(correspondences: List<correspondence>) {
    var outputSettingStrings = MutableList(correspondences.size) { "" }

    fun update(idx: Int, numsString: String) {
        var numStringTmp = numsString
        if ("--selected" in numsString) {
            numStringTmp = numsString.replace("--selected", "")
        }
        outputSettingStrings[idx] = numsString
    }

    fun get(idx: Int): String {
        return outputSettingStrings[idx]
    }

    fun getAll(): List<String> {
        return outputSettingStrings
    }
}

// メインテストコード
fun EditApp() = singleWindowApplication(
    title = "Edit Screen", state = WindowState(width = 1000.dp, height = 600.dp)
) {
    var isOpenEditWindow by remember { mutableStateOf(true) }
    var filePath by remember { mutableStateOf("") }
    var rawData by remember { mutableStateOf(arrayListOf<List<String>>()) }
//    filePath = "kotlin_sample/V3/SP00FUJISAKI/order20220916_174559_test (1).csv"
//    filePath = "kotlin_sample/V3/SP00ONEPALETTE/test_order_2022-10-28 19_04_59 +0900.csv"
    filePath = "kotlin_sample/V3/SP00COMEDICAL/サンプルデータ～配送明細.csv"
//    filePath = "kotlin_sample/V3/SP00EUCALY/order_20221006152145.csv"
    rawData = getRawData(filePath)
    if (isOpenEditWindow) {
        EditWindow(rawData, filePath)
    }
}

fun main() {
    EditApp()
}