const { ResourceManagementClient } = require("@azure/arm-resources");
const { DefaultAzureCredential } = require("@azure/identity");

async function toArray(asyncIterator){ 
    const arr=[]; 
    for await(const i of asyncIterator) arr.push(i); 
    return arr;
}

module.exports = async function (context, req) {
    context.log('JavaScript HTTP trigger function processed a request.');
    
    var subscription_id = "91f277f3-0f24-4e0f-a841-15d287766a59";
    const credential = new DefaultAzureCredential();

    var resource_client = new ResourceManagementClient(credential, subscription_id);
    var rgs = resource_client.resourceGroups.list();
    var temp = await toArray(rgs);
    var out = temp.map( i => {
        return i.name;
    });
    context.res = {
        // status: 200, /* Defaults to 200 */
        body: "TESTINGCICD"
    };
}