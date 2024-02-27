import csv

# Open the input CSV file
with open('input.csv', 'r', encoding='utf-8') as csv_file:
    reader = csv.DictReader(csv_file)
    
    # Define the fieldnames for the output CSV file
    fieldnames = reader.fieldnames[:]  # Copy the fieldnames from input
    fieldnames.remove('description')  # Remove the 'description' field
    fieldnames.remove('last-modified')
    fieldnames.remove('location')
    fieldnames.remove('dtstamp')
    fieldnames.extend(['Matiere', 'Enseignants', 'TD', 'Salle', 'Type', 'Memo'])  # Add new fields
    
    # Remove the 'summary' field if it exists
    if 'summary' in fieldnames:
        fieldnames.remove('summary')
    
    # Open the output CSV file
    with open('output.csv', 'w', newline='', encoding='utf-8') as output_file:
        writer = csv.DictWriter(output_file, fieldnames=fieldnames)
        writer.writeheader()  # Write the header row
        
        # Process each row in the input CSV file
        for row in reader:
            description = row['description']
            
            # Split the description into components
            components = description.split('\\n')
            
            # Extract relevant information
            new_fields = {'Matiere': '', 'Enseignants': '', 'TD': '', 'Salle': '', 'Type': '', 'Memo': ''}
            for component in components:
                if 'Matiere' in component:
                    new_fields['Matiere'] = component.replace('Matiere : ', '').strip()
                elif 'Enseignant' in component or 'Enseignants' in component:
                    new_fields['Enseignants'] = component.replace('Enseignant : ', '').replace('Enseignants : ', '').strip()
                elif 'TD' in component:
                    new_fields['TD'] = component.replace('TD : ', '').strip()
                elif 'Salle' in component:
                    new_fields['Salle'] = component.replace('Salle : ', '').strip()
                elif 'Type' in component:
                    new_fields['Type'] = component.replace('Type : ', '').strip()
                elif 'Memo' in component:
                    new_fields['Memo'] = component.replace('Memo : ', '').strip()
            
            # Write the row to the output CSV file
            new_row = {key: row[key] for key in row if key != 'description' and key != 'summary' and key !='last-modified' and key !='location' and key !='dtstamp'}  # Copy existing fields except 'description' and 'summary'
            new_row.update(new_fields)  # Add new fields
            writer.writerow(new_row)
