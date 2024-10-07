extraImageCount = 0;

$(document).ready(function(){
    $("input[name='extraImage']").each(function(index){
        extraImageCount++;
        $(this).change(function(){
            showExtraImageThumbnail(this, index);
        });
    });

    $("a[name='linkRemoveExtraImage']").each(function(index){
        $(this).click(function(){
            removeExtraImage(index);
        });
    })
});

function showExtraImageThumbnail(fileInput, index){
    if(!checkFileSize(fileInput)) return;

    let file = fileInput.files[0];

    let fileName = file.name;
    let imageNameHiddenField = $('#imageName' + index);
    if(imageNameHiddenField.length) imageNameHiddenField.val(fileName);

    let reader = new FileReader();
    reader.onload = function(e){
        $("#extraThumbnail" + index).attr("src", e.target.result);
    };
    reader.readAsDataURL(file);

    if(index >= extraImageCount - 1) addNextExtraImageSection(index + 1);
}

function addNextExtraImageSection(index){
    console.log(extraImageCount)
    htmlExtaImage = `
        <div class="col border m-3 p-2" id="divExtraImage${index}">
            <div id="extraImageHeader${index}"><label>Extra Image #${index + 1}</label></div>
            <div>
                <img src="${defaultImageThumbnailSrc}" id="extraThumbnail${index}"
                     alt="Extra image #${index + 1} preview" class="img-fluid" />
            </div>
            <div>
                <input type="file" name="extraImage" 
                onchange="showExtraImageThumbnail(this, ${index})"
                accept="image/png, image/jpeg"/>
            </div>
        </div>`;

    htmlLinkRemove = `
        <a class="btn fas fa-times-circle fa-2x icon-dark float-right" 
            href="javascript:removeExtraImage(${index - 1})"
            title="Remove this image"></a>`;

    $("#divProductImages").append(htmlExtaImage);
    $("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);

    extraImageCount++;
}

function removeExtraImage(index){
    $("#divExtraImage" + index).remove();
}