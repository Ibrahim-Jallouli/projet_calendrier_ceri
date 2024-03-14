import csv

# Open the input CSV file
with open('input-AI-classique.csv', 'r', encoding='utf-8') as csv_file:
    reader = csv.DictReader(csv_file)
    
    # Define the fieldnames for the output CSV file
    fieldnames = reader.fieldnames[:]  # Copy the fieldnames from input
    fieldnames.remove('description')  # Remove the 'description' field
    
    # Add new fields
    fieldnames.extend(['Matiere', 'Enseignants', 'Type', 'Memo'])
    
    # Open the output CSV file
    with open('output.csv', 'w', newline='', encoding='utf-8') as output_file:
        writer = csv.DictWriter(output_file, fieldnames=fieldnames)
        writer.writeheader()  # Write the header row
        
        # Process each row in the input CSV file
        for row in reader:
            description = row['description']
            
            # Split the description into components
            components = description.split('\\n')
            
            # Initialize variables for extracted information
            matiere = ''
            enseignants = ''
            type_info = ''
            memo = ''
            
            # Extract relevant information
            for component in components:
                if 'Matiere' in component:
                    matiere = component.replace('Matiere : ', '').strip()
                elif 'Enseignant' in component or 'Enseignants' in component:
                    enseignants = component.replace('Enseignant : ', '').replace('Enseignants : ', '').strip()
                elif 'Type' in component:
                    type_info = component.replace('Type : ', '').strip()
                elif 'Memo' in component:
                    memo = component.replace('Memo : ', '').strip()
            
            # Remove the 'description' field from the row
            del row['description']
            
            # Write the row to the output CSV file
            writer.writerow({
                **row, 
                'Matiere': matiere, 
                'Enseignants': enseignants, 
                'Type': type_info, 
                'Memo': memo
            })
