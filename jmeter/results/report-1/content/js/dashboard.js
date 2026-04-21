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

    var data = {"OkPercent": 99.98333333333333, "KoPercent": 0.016666666666666666};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.40670833333333334, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.239, 500, 1500, "POST /orders"], "isController": false}, {"data": [0.0, 500, 1500, "GET /books"], "isController": false}, {"data": [0.9305, 500, 1500, "GET /orders/bulk-async/{taskId}"], "isController": false}, {"data": [0.0465, 500, 1500, "POST /books"], "isController": false}, {"data": [0.6345, 500, 1500, "GET /orders/{id}"], "isController": false}, {"data": [0.0095, 500, 1500, "GET /books/{id}"], "isController": false}, {"data": [0.664, 500, 1500, "POST /orders/bulk"], "isController": false}, {"data": [0.187, 500, 1500, "POST /authors"], "isController": false}, {"data": [0.213, 500, 1500, "POST /publishers"], "isController": false}, {"data": [0.861, 500, 1500, "POST /orders/bulk-async"], "isController": false}, {"data": [0.122, 500, 1500, "POST /clients"], "isController": false}, {"data": [0.9735, 500, 1500, "GET /orders/bulk-async/tasks"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 12000, 2, 0.016666666666666666, 7380.231083333334, 0, 59691, 3654.5, 26131.999999999996, 39473.59999999999, 46878.85, 131.2436428860477, 1515.0382806775178, 32.54393756903962], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["POST /orders", 1000, 0, 0.0, 7327.295000000007, 270, 52185, 3426.5, 21545.799999999992, 35742.6, 45646.9, 12.048628263672182, 4.177061746207694, 3.0239233044567877], "isController": false}, {"data": ["GET /books", 1000, 1, 0.1, 24098.117000000024, 2004, 59691, 22732.0, 46359.4, 47910.0, 52758.8, 11.27713560755568, 53.87858577671272, 2.0814244431914295], "isController": false}, {"data": ["GET /orders/bulk-async/{taskId}", 1000, 0, 0.0, 1090.8500000000006, 0, 39466, 143.0, 397.0, 2599.8999999999996, 33387.83000000001, 17.928536852107502, 5.080394080445345, 3.9043590996288793], "isController": false}, {"data": ["POST /books", 1000, 0, 0.0, 6413.385999999996, 94, 34121, 5177.0, 11420.7, 17258.8, 27311.73, 17.389489792369492, 8.049431798421034, 5.519125178242271], "isController": false}, {"data": ["GET /orders/{id}", 1000, 1, 0.1, 4811.309999999996, 252, 55623, 494.0, 17113.299999999992, 36546.149999999994, 45356.4, 13.070186903672722, 4.52989192589204, 2.297544847078813], "isController": false}, {"data": ["GET /books/{id}", 1000, 0, 0.0, 23317.16900000004, 428, 55261, 22865.5, 45172.3, 46061.65, 51675.45, 11.484484461492524, 5.316060190183062, 2.018757034246733], "isController": false}, {"data": ["POST /orders/bulk", 1000, 0, 0.0, 5532.875000000004, 12, 56576, 481.0, 25053.299999999996, 41903.49999999996, 49594.430000000015, 13.77258704275011, 7.680880335155905, 4.398081995096959], "isController": false}, {"data": ["POST /authors", 1000, 0, 0.0, 5191.188, 25, 9860, 6035.5, 7954.0, 8329.9, 8636.98, 55.07214450930719, 16.24198011895583, 17.96298463487168], "isController": false}, {"data": ["POST /publishers", 1000, 0, 0.0, 3384.527000000002, 12, 9100, 3894.5, 5297.9, 5995.7, 7314.0, 76.40586797066015, 15.445326826100246, 17.98224041106357], "isController": false}, {"data": ["POST /orders/bulk-async", 1000, 0, 0.0, 2626.049999999999, 0, 39646, 214.5, 6034.799999999992, 21671.54999999996, 38501.26, 14.563672375626238, 4.108633845610509, 4.736037989339391], "isController": false}, {"data": ["POST /clients", 1000, 0, 0.0, 4505.242000000004, 20, 9349, 4912.5, 6887.4, 7377.499999999999, 8265.720000000001, 41.3992962119644, 8.166658041813289, 10.754114055061063], "isController": false}, {"data": ["GET /orders/bulk-async/tasks", 1000, 0, 0.0, 264.76400000000024, 3, 13957, 147.0, 347.0, 389.94999999999993, 4372.040000000004, 61.30080304051983, 7988.123029274965, 11.49390057009747], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["500", 2, 100.0, 0.016666666666666666], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 12000, 2, "500", 2, "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": ["GET /books", 1000, 1, "500", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["GET /orders/{id}", 1000, 1, "500", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
