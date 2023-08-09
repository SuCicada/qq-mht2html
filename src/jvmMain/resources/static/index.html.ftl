<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>QQ History</title>
</head>
<body>

<h1>Lain 的 wired </h1>
<h2>QQ History Archive</h2>

every file has 4000 messages

<ul>
    <table>
        <#list items as item>
            <tr>
                <td>
                    <li>
                        <a href="${item.name}">${item.name}</a> &nbsp;
                    </li>
                </td>
                <td>
                    ${item.dateRange[0]} - ${item.dateRange[1]}
                </td>
            </tr>
        </#list>
    </table>
</ul>
</body>
</html>
