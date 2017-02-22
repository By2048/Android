ers.WebGrid" /> instance is sorted by.</summary>
      <returns>The full name of the query-string field that is used to specify the name of the data column that the grid is sorted by.</returns>
    </member>
    <member name="M:System.Web.Helpers.WebGrid.Table(System.String,System.String,System.String,System.String,System.String,System.String,System.String,System.Boolean,System.Boolean,System.String,System.Collections.Generic.IEnumerable{System.Web.Helpers.WebGridColumn},System.Collections.Generic.IEnumerable{System.String},System.Func{System.Object,System.Object},System.Object)">
      <summary>Returns the HTML markup that is used to render the <see cref="T:System.Web.Helpers.WebGrid" /> instance.</summary>
      <returns>The HTML markup that represents the fully-populated <see cref="T:System.Web.Helpers.WebGrid" /> instance.</returns>
      <param name="tableStyle">The name of the CSS class that is used to style the whole table.</param>
      <param name="headerStyle">The name of the CSS class that is used to style the table header.</param>
      <param name="footerStyle">The name of the CSS class that is used to style the table footer.</param>
      <param name="rowStyle">The name of the CSS class that is used to style each table row.</param>
      <param name="alternatingRowStyle">The name of the CSS class that is used to style even-numbered table rows.</param>
      <param name="selectedRowStyle">The name of the CSS class that is used use to style the selected table row.</param>
      <param name="caption">The table caption.</param>
      <param name="displayHeader">true to display the table header; otherwise, false. The default is true.</param>
      <param name="fillEmptyRows">true to insert additional rows in the last page when there are insufficient data items to fill the last page; otherwise, false. The default is false. Additional rows are populated using the text specified by the <paramref name="emptyRowCellValue" /> parameter.</param>
      <param name="emptyRowCellValue">The text that is used to populate additional rows in the 