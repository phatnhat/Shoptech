let buttonLoad4States;
let dropDownCountry4States;
let dropDownStates;
let buttonAddState;
let buttonUpdateState;
let buttonDeleteState;
let fieldStateName;

$(document).ready(function() {
    buttonLoad4States = $("#buttonLoadCountriesForStates");
    dropDownCountry4States = $("#dropDownCountriesForStates");
    dropDownStates = $("#dropDownStates");
    buttonAddState = $("#buttonAddState");
    buttonUpdateState = $("#buttonUpdateState");
    buttonDeleteState = $("#buttonDeleteState");
    fieldStateName = $("#fieldStateName");

    fieldStateName.prop("disabled", true);

    buttonLoad4States.click(function() {
        loadCountries4States();
    });

    dropDownCountry4States.on("change", function() {
        loadStates4Country();
        buttonAddState.prop("disabled", false);
    });

    dropDownStates.on("change", function() {
        changeFormStateToSelectedState();
    });

    buttonAddState.click(function() {
        if ($(this).val() === "Add") {
            if(checkUniqueState()){
                addState();
            }
        } else {
            changeFormStateToNew();
        }
    });

    buttonUpdateState.click(function() {
        if(checkUniqueState()) {
            updateState();
        }
    });

    buttonDeleteState.click(function() {
        deleteState();
    });
});

function deleteState() {
    let stateId = dropDownStates.val();

    let url = contextPath + "states/delete/" + stateId;

    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function() {
        $("#dropDownStates option[value='" + stateId + "']").remove();
        changeFormStateToNew();
        showToastMessage("The state has been deleted");
    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

function updateState() {

    if (!validateFormState()) return;

    let url = contextPath + "states/save";
    let stateId = dropDownStates.val();
    let stateName = fieldStateName.val();

    let selectedCountry = $("#dropDownCountriesForStates option:selected");
    let countryId = selectedCountry.val();
    let countryName = selectedCountry.text();

    let jsonData = {id: stateId, name: stateName, country: {id: countryId, name: countryName}};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function(stateId) {
        $("#dropDownStates option:selected").text(stateName);
        showToastMessage("The state has been updated");
        changeFormStateToNew();
    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

function addState() {

    if (!validateFormState()) return;

    let url = contextPath + "states/save";
    let stateName = fieldStateName.val();

    let selectedCountry = $("#dropDownCountriesForStates option:selected");
    let countryId = selectedCountry.val();
    let countryName = selectedCountry.text();

    let jsonData = {name: stateName, country: {id: countryId, name: countryName}};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function(stateId) {
        selectNewlyAddedState(stateId, stateName);
        showToastMessage("The new state has been added");
    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });

}

function selectNewlyAddedState(stateId, stateName) {
    $("<option>").val(stateId).text(stateName).appendTo(dropDownStates);

    //$("#dropDownStates option[value='" + stateId + "']").prop("selected", true);

    fieldStateName.val("").focus();
}

function changeFormStateToNew() {
    buttonAddState.val("Add");

    buttonUpdateState.prop("disabled", true);
    buttonDeleteState.prop("disabled", true);

    fieldStateName.prop("disabled", false);

    fieldStateName.val("").focus();
}

function changeFormStateToSelectedState() {
    buttonAddState.prop("value", "New");
    buttonUpdateState.prop("disabled", false);
    buttonDeleteState.prop("disabled", false);

    fieldStateName.prop("disabled", false);

    let selectedStateName = $("#dropDownStates option:selected").text();
    fieldStateName.val(selectedStateName);

}

function loadStates4Country() {
    let selectedCountry = $("#dropDownCountriesForStates option:selected");
    let countryId = selectedCountry.val();
    let url = contextPath + "states/list_by_country/" + countryId;

    $.get(url, function(responseJSON) {
        dropDownStates.empty();

        $.each(responseJSON, function(index, state) {
            $("<option>").val(state.id).text(state.name).appendTo(dropDownStates);
        });

    }).done(function() {
        changeFormStateToNew();
        showToastMessage("All states have been loaded for country " + selectedCountry.text());
    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

function loadCountries4States() {
    let url = contextPath + "countries/list";
    $.get(url, function(responseJSON) {
        dropDownCountry4States.empty();
        dropDownStates.empty();

        $.each(responseJSON, function(index, country) {
            $("<option>").val(country.id).text(country.name).appendTo(dropDownCountry4States);
        });

    }).done(function() {
        buttonLoad4States.val("Refresh Country List");
        showToastMessage("All countries have been loaded");
    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

function validateFormState() {
    let formState = document.getElementById("formState");
    if (!formState.checkValidity()) {
        formState.reportValidity();
        return false;
    }

    return true;
}

function checkUniqueState() {

    console.log("checkUnique is working");

    let countryId = dropDownCountry4States.val();
    let stateId = 0;
    if(dropDownStates.val()){
        stateId = dropDownStates.val();
    }
    let stateName = $("#fieldStateName").val();

    console.log(stateName);

    let csrfValue = $("input[name='_csrf']").val();

    let jsonData = {country_id: countryId, state_id: stateId, name: stateName, _csrf: csrfValue};

    let checkUniqueUrl = contextPath + "states/check_unique";

    let flag_check = false;

    $.ajax({
        type: 'POST',
        url: checkUniqueUrl,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json',
        async: false
    }).done(function(response) {
        if (response === "OK") {
            flag_check =  true;
        } else if (response === "Duplicate") {
            showToastMessage("State already added :  " + stateName);
            flag_check = false;
        } else {
            showToastMessage("Unknown response from server");
            flag_check = false;
        }
    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
        flag_check = false;
    });

    return flag_check;
}	 