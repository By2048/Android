r>
    <member name="T:System.Web.Helpers.ObjectInfo">
      <summary>Renders the property names and values of the specified object and of any subobjects that it references.</summary>
    </member>
    <member name="M:System.Web.Helpers.ObjectInfo.Print(System.Object,System.Int32,System.Int32)">
      <summary>Renders the property names and values of the specified object and of any subobjects.</summary>
      <returns>For a simple variable, returns the type and the value. For an object that contains multiple items, returns the property name or key and the value for each property.</returns>
      <param name="value">The object to render information for.</param>
      <param name="depth">Optional. Specifies the depth of nested subobjects to render information for. The default is 10.</param>
      <param name="enumerationLength">Optional. Specifies the maximum number of characters that the method displays for object values. The default is 1000.</param>
      <exception cref="T:System.ArgumentOutOfRangeException">
        <paramref name="depth" /> is less than zero.</exception>
      <exception cref="T:System.ArgumentOutOfRangeException">
        <paramref name="enumerationLength" /> is less than or equal to zero.</exception>
    </member>
    <member name="T:System.Web.Helpers.ServerInfo">
      <summary>Displays information about the web server environment that hosts the current web page.</summary>
    </member>
    <member name="M:System.Web.Helpers.ServerInfo.GetHtml">
      <summary>Displays information about the web server environment.</summary>
      <returns>A string of name-value pairs that contains information about the web server. </returns>
    </member>
    <member name="T:System.Web.Helpers.SortDirection">
      <summary>Specifies the direction in which to sort a list of items.</summary>
    </member>
    <member name="F:System.Web.Helpers.SortDirection.Ascending">
      <summary>Sort from smallest to largest —for example, from 1 to 10.</summary>
    </member>
    <member name="F:System.Web.Helpers.SortDirection.Descending">
      <summary>Sort from largest to smallest — for example, from 10 to 1.</summary>
    </member>
    <member name="T:System.Web.Helpers.WebCache">
      <summary>Provides a cache to store frequently accessed data.</summary>
    </member>
    <member name="M:System.Web.Helpers.WebCache.Get(System.String)">
      <summary>Retrieves the specified item from the <see cref="T:System.Web.Helpers.WebCache" /> object.</summary>
      <returns>The item retrieved from the cache, or null if the item is not found.</returns>
      <param name="key">The id