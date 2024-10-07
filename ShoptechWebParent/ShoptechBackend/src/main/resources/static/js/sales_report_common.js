let MILLISECONDS_A_DAY = 24 * 60 * 60 * 1000;

function setupButtonEventHandlers(reportType, callbackFunction){
    initCustomDateRange(reportType);

    $(".button-sales-by" + reportType).on("click", function(){
        $(".button-sales-by" + reportType).each(function(e){
            $(this).removeClass('btn-primary').addClass('btn-light');
        });
        $(this).removeClass('btn-light').addClass('btn-primary');

        let period = $(this).attr('period');
        if(period){
            callbackFunction(period);
            $("#divCustomDateRange" + reportType).addClass("d-none");
        }else{
            callbackFunction("custom");
            $("#divCustomDateRange" + reportType).removeClass("d-none");
        }
    });

    $("#buttonViewReportByDateRange" + reportType).on("click", function(e){
        validateDateRange(reportType, callbackFunction);
    });
}

function validateDateRange(reportType, callbackFunction){
    let days = calculateDays(reportType);

    startDateField = $("#startDate"  + reportType);
    startDateField.get(0).setCustomValidity("");

    if(days >= 7 && days <= 30){
        callbackFunction("custom");
    }else{
        startDateField.get(0).setCustomValidity("Dates must be in the range of 7..30 days");
        startDateField.get(0).reportValidity();
    }
}

function calculateDays(reportType){
    startDateField = $("#startDate"  + reportType);
    endDateField = $("#endDate"  + reportType);

    let startDate = new Date(startDateField.val());
    let endDate = new Date(endDateField.val());

    let differenceInMilliseconds = endDate - startDate;
    return differenceInMilliseconds / MILLISECONDS_A_DAY;
}

function initCustomDateRange(reportType){
    startDateField = $("#startDate"  + reportType);
    endDateField = $("#endDate"  + reportType);

    let toDate = new Date();
    endDateField.val(parseDateToString(toDate));

    let fromDate = new Date();
    fromDate.setDate(toDate.getDate() - 30);
    startDateField.val(parseDateToString(fromDate));
}

function parseDateToString(date){
    return date.toISOString().split("T")[0];
}


function formatCurrency(amount){
    let formattedAmount = $.number(amount, decimalDigits, decimalPointType, thousandsPointType);
    return prefixCurrencySymbol + formattedAmount + suffixCurrencySymbol;
}

function getChartTitle(period){
    if(period == 'last_7_days') return 'Sales in Last 7 Days';
    if(period == 'last_28_days') return 'Sales in Last 28 Days';
    if(period == 'last_6_months') return 'Sales in Last 6 Months';
    if(period == 'last_years') return 'Sales in Last Years';
    if(period == 'custom') return 'Custom Date Range';

    return "";
}

function getDenominator(period, reportType){
    if(period == 'last_7_days') return 7;
    if(period == 'last_28_days') return 28;
    if(period == 'last_6_months') return 6;
    if(period == 'last_year') return 12;
    if(period == 'custom') return calculateDays(reportType);

    return 7;
}

function setSalesAmount(period, reportType, labelTotalItems){
    let denominator = getDenominator(period, reportType);

    $("#textTotalGrossSales" + reportType).text(formatCurrency(totalGrossSales));
    $("#textTotalNetSales" + reportType).text(formatCurrency(totalNetSales));
    $("#textAvgGrossSales" + reportType).text(formatCurrency(totalGrossSales / denominator));
    $("#textAvgNetSales" + reportType).text(formatCurrency(totalNetSales / denominator));
    $("#labelTotalItems" + reportType).text(labelTotalItems);
    $("#textTotalItems" + reportType).text(totalItems);
}

function formatChartData(data, columnIndex1, columnIndex2){
    let formatter = new google.visualization.NumberFormat({
        prefix: prefixCurrencySymbol,
        suffix: suffixCurrencySymbol,
        decimalSymbol: decimalPointType,
        groupingSymbol: thousandsPointType,
        fractionDigits: decimalDigits
    });

    formatter.format(data, columnIndex1);
    formatter.format(data, columnIndex2);
}