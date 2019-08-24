class SearchFilter extends React.Component {

       state = { searchString: '' }
       handleChange = (e) => {
         this.setState({ searchString:e.target.value });
       }
       render() {
         var libraries = this.props.items,
             searchString = this.state.searchString.trim().toLowerCase();
         if (searchString.length > 0) {
           libraries = libraries.filter(function(i) {
             return i.name.toLowerCase().match( searchString );
           });
         }
         return (
           <div>
              <input type="text" value={this.state.searchString} onChange={this.handleChange} placeholder="Type here..."/>
              <ul>
                {libraries.map(function(i) {
                    return <li>{i.name} <a href={i.url}>{i.url}</a></li>;
                }) } 
              </ul>
           </div>
         );
       }
    }
    
    React.render(
      <SearchFilter />,
      document.getElementById('content') 
    )