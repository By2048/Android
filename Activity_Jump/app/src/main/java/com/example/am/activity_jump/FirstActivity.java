﻿@model IEnumerable<MyStore.Domain.Concrete.Category>
@{
    Layout = null;
}

<!DOCTYPE html>

<html>
<head>
    <meta name="viewport" content="width=device-width" />
    <link href="~/Content/bootstrap.css" rel="stylesheet">
    <link href="~/Content/bootstrap-theme.css" rel="stylesheet">
    <link href="~/Content/Pagedlist.css" rel="stylesheet">
    <title>Menu</title>
</head>
<body>
    <div>

        @Html.ActionLink("主页", "List", "Product", null,
                        new { @class = "btn btn-block btn-default btn-lg" })

        @foreach (var category in Model)
        {
            @Html.RouteLink(category.Name,
            new { controller = "Product", action = "List", categoryId = category.Id, page = 1 },
            new { @class = "btn btn-block btn-default btn-lg" + (category.Id == ViewBag.CurrentCategoryId ? "btn-primary" : "") })
        }
    </div>
</body>
</html>
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            