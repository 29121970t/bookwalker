/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 99.99166666666666, "KoPercent": 0.008333333333333333};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.34608333333333335, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.134, 500, 1500, "POST /orders"], "isController": false}, {"data": [0.0015, 500, 1500, "GET /books"], "isController": false}, {"data": [0.9145, 500, 1500, "GET /orders/bulk-async/{taskId}"], "isController": false}, {"data": [0.049, 500, 1500, "POST /books"], "isController": false}, {"data": [0.3665, 500, 1500, "GET /orders/{id}"], "isController": false}, {"data": [0.008, 500, 1500, "GET /books/{id}"], "isController": false}, {"data": [0.558, 500, 1500, "POST /orders/bulk"], "isController": false}, {"data": [0.104, 500, 1500, "POST /authors"], "isController": false}, {"data": [0.098, 500, 1500, "POST /publishers"], "isController": false}, {"data": [0.9, 500, 1500, "POST /orders/bulk-async"], "isController": false}, {"data": [0.0865, 500, 1500, "POST /clients"], "isController": false}, {"data": [0.933, 500, 1500, "GET /orders/bulk-async/tasks"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 12000, 1, 0.008333333333333333, 9992.254999999981, 0, 61258, 2488.0, 38948.0, 41640.95, 49024.0, 95.34630572792932, 1111.4753500003974, 23.650353178607467], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["POST /orders", 1000, 0, 0.0, 17250.67700000001, 471, 61258, 9587.5, 42124.299999999996, 44027.8, 50939.46000000001, 8.271982794275788, 2.8758065183224417, 2.076073806766482], "isController": false}, {"data": ["GET /books", 1000, 1, 0.1, 30176.71, 1356, 57581, 34179.0, 44659.59999999999, 49506.09999999999, 52457.93, 8.122619057288833, 38.807303808289944, 1.4991943377222552], "isController": false}, {"data": ["GET /orders/bulk-async/{taskId}", 1000, 0, 0.0, 2154.4539999999924, 0, 36069, 224.5, 472.0, 25253.89999999994, 33444.72, 9.38297552919982, 2.668411948984762, 2.0433628349722266], "isController": false}, {"data": ["POST /books", 1000, 0, 0.0, 16501.08500000002, 43, 57668, 9463.0, 39618.7, 40924.7, 48608.92, 10.439285117755137, 4.8322472127108735, 3.313249671162519], "isController": false}, {"data": ["GET /orders/{id}", 1000, 0, 0.0, 5852.172000000008, 27, 48241, 945.0, 21497.499999999996, 32483.44999999997, 41976.94, 8.434619050430587, 2.932348029251259, 1.4908848126249379], "isController": false}, {"data": ["GET /books/{id}", 1000, 0, 0.0, 29344.39499999999, 484, 55864, 33022.0, 43735.0, 48226.19999999998, 51067.71, 8.153614089445146, 3.77423152187207, 1.4332524766602797], "isController": false}, {"data": ["POST /orders/bulk", 1000, 0, 0.0, 3333.3150000000087, 12, 49120, 716.0, 4193.299999999993, 29015.299999999934, 42062.4, 8.809563662311806, 4.920967201994485, 2.8132102710702735], "isController": false}, {"data": ["POST /authors", 1000, 0, 0.0, 2887.236, 19, 6386, 2592.5, 4598.199999999999, 5062.849999999999, 5711.0, 60.08532115604158, 17.72047557531695, 19.59814186144325], "isController": false}, {"data": ["POST /publishers", 1000, 0, 0.0, 3425.201000000001, 11, 7225, 3632.5, 4944.9, 5204.699999999997, 6024.63, 75.79777154551657, 15.322401083908133, 17.839123967255365], "isController": false}, {"data": ["POST /orders/bulk-async", 1000, 0, 0.0, 2604.2979999999984, 0, 36067, 351.0, 654.8, 29941.99999999998, 34076.100000000006, 8.980530210503629, 2.533491764853797, 2.920426328220418], "isController": false}, {"data": ["POST /clients", 1000, 0, 0.0, 4967.717999999997, 9, 31550, 2965.0, 11049.3, 16507.8, 29055.950000000004, 21.42015636714148, 4.225460533361893, 5.564220306308235], "isController": false}, {"data": ["GET /orders/bulk-async/tasks", 1000, 0, 0.0, 1409.7990000000032, 2, 36068, 142.5, 394.0, 3044.8999999999996, 33662.340000000004, 12.16826273712902, 1602.1799205032185, 2.281549263211691], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["500", 1, 100.0, 0.008333333333333333], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 12000, 1, "500", 1, "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": ["GET /books", 1000, 1, "500", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
